package com.lms.utils;

import com.lms.annotations.ExcelColumn;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

public class FileOperations {

    public <T> List<T> readExcelFile(Class<T> type, int batchSize) throws Exception {

        Properties properties = new Properties();
        List<T> result = new ArrayList<>();

        try (InputStream propStream = FileOperations.class.getClassLoader().getResourceAsStream("application.properties")) {

            properties.load(propStream);
            String fileName = properties.getProperty("import.file.name");

            InputStream is = FileOperations.class.getClassLoader().getResourceAsStream(fileName);

            assert is != null;
            OPCPackage pkg = OPCPackage.open(is);
            XSSFReader reader = new XSSFReader(pkg);
            SharedStringsTable sst = (SharedStringsTable) reader.getSharedStringsTable();
            StylesTable stylesTable = reader.getStylesTable();

            XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();

            SheetHandler<T> handler = new SheetHandler<>(type, batchSize, result, sst, stylesTable);
            parser.setContentHandler(handler);

            Iterator<InputStream> sheets = reader.getSheetsData();
            if (sheets.hasNext()) {
                parser.parse(new InputSource(sheets.next()));
            }
            pkg.close();
        }

        return result;
    }

    private static class SheetHandler<T> extends DefaultHandler {

        private final Class<T> type;
        private final int batchSize;
        private final List<T> result;
        private final SharedStringsTable sst;
        private final StylesTable stylesTable;

        private final Map<String, Integer> columnMap = new HashMap<>();
        private final Map<Integer, String> currentRow = new HashMap<>();
        private final List<T> batch = new ArrayList<>();

        // Cell type flags
        private boolean isSharedString;
        private boolean isDateCell;
        private boolean isBooleanCell;
        private boolean isFormulaCell;

        private String lastContents = "";
        private int currentRowNumber = 0;
        private int currentColumnIndex = -1;

        // Internal prefix to tag date values before mapping to fields
        private static final String DATE_PREFIX = "__DATE__:";

        public SheetHandler(Class<T> type, int batchSize, List<T> result, SharedStringsTable sst, StylesTable stylesTable) {
            this.type = type;
            this.batchSize = batchSize;
            this.result = result;
            this.sst = sst;
            this.stylesTable = stylesTable;
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) {
            if ("c".equals(name)) {
                String cellRef = attributes.getValue("r"); // e.g. C2
                currentColumnIndex = getColumnIndex(cellRef);

                String cellType = attributes.getValue("t");
                isSharedString = "s".equals(cellType);
                isBooleanCell = "b".equals(cellType);
                isFormulaCell = false;
                isDateCell = false;

                // Detect date cells: numeric cells (no type attr) with a date-formatted style
                if (cellType == null || cellType.isEmpty() || "n".equals(cellType))  {
                    String styleIndexStr = attributes.getValue("s");
                    if (styleIndexStr != null && stylesTable != null) {
                        try {
                            int styleIndex = Integer.parseInt(styleIndexStr);
                            XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                            short formatIndex = style.getDataFormat();
                            String formatString = style.getDataFormatString();
                            isDateCell = DateUtil.isADateFormat(formatIndex, formatString);
                        } catch (Exception e) {
                            // ignore style parse errors
                        }
                    }
                }
            }

            // Formula cells: the value is inside <v>, but the cell has a <f> child
            if ("f".equals(name)) {
                isFormulaCell = true;
            }

            lastContents = "";
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            lastContents += new String(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String name) {

            if ("v".equals(name)) {
                String value = lastContents;

                if (isSharedString) {
                    int idx = Integer.parseInt(value);
                    value = sst.getItemAt(idx).getString();
                } else if (isBooleanCell) {
                    // Excel stores booleans as "1" or "0"
                    value = "1".equals(value) ? "true" : "false";
                } else if (isDateCell) {
                    // Tag date serial numbers so endElement("row") knows to convert them
                    value = DATE_PREFIX + value;
                }

                currentRow.put(currentColumnIndex, value);
            }

            if ("row".equals(name)) {
                if (currentRowNumber == 0) {
                    // Header row: build column name → index map
                    for (Map.Entry<Integer, String> entry : currentRow.entrySet()) {
                        String header = entry.getValue();
                        if (header != null) {
                            columnMap.put(header.trim().toLowerCase(), entry.getKey());
                        }
                    }
                } else {
                    boolean allNull = currentRow.values().stream().allMatch(Objects::isNull);
                    if (!allNull) {
                        try {
                            T obj = type.getDeclaredConstructor().newInstance();

                            for (Field field : type.getDeclaredFields()) {
                                ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
                                if (annotation == null) continue;

                                String columnName = annotation.value().trim().toLowerCase();
                                if (!columnMap.containsKey(columnName)) continue;

                                String value = currentRow.get(columnMap.get(columnName));
                                field.setAccessible(true);
                                System.out.println("Line "+ columnName);
                                System.out.println("Value "+ value);
                                if (value == null) continue;

                                Class<?> fieldType = field.getType();

                                if (fieldType == String.class) {
                                    field.set(obj, value);
                                } else if (fieldType == float.class || fieldType == Float.class) {
                                    field.set(obj, Float.parseFloat(value));
                                } else if (fieldType == double.class || fieldType == Double.class) {
                                    field.set(obj, Double.parseDouble(value));
                                } else if (fieldType == int.class || fieldType == Integer.class) {
                                    // Excel may store integers as "25.0" — handle that
                                    field.set(obj, (int) Double.parseDouble(value));
                                } else if (fieldType == long.class || fieldType == Long.class) {
                                    field.set(obj, (long) Double.parseDouble(value));
                                } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                                    // Handle plain "true"/"false", "1"/"0", or formula results like "=FALSE()"
                                    String normalized = value.trim().toLowerCase();
                                    boolean boolValue = "true".equals(normalized)
                                            || "1".equals(normalized)
                                            || "yes".equals(normalized);
                                    field.set(obj, boolValue);
                                } else if (fieldType == Date.class) {
                                    if (value.startsWith(DATE_PREFIX)) {
                                        double serial = Double.parseDouble(value.substring(DATE_PREFIX.length()));
                                        Date date = DateUtil.getJavaDate(serial);
                                        field.set(obj, date);
                                    }
                                }
                            }
                            batch.add(obj);
                            if (batch.size() == batchSize) {
                                result.addAll(batch);
                                batch.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                currentRow.clear();
                currentRowNumber++;
            }
        }

        @Override
        public void endDocument() {
            if (!batch.isEmpty()) {
                result.addAll(batch);
            }
        }

        private int getColumnIndex(String cellRef) {
            int col = 0;
            for (int i = 0; i < cellRef.length(); i++) {
                char ch = cellRef.charAt(i);
                if (Character.isLetter(ch)) {
                    col = col * 26 + (ch - 'A' + 1);
                } else {
                    break;
                }
            }
            return col - 1; // zero-based
        }
    }
}