package com.lms.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ColumnDef<T> {
    private final String header;
    private final int width;
    private final Function<T, String> valueExtractor;
}
