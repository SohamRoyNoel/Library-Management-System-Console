package com.lms.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Function;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ColumnDef<T> {
    private final String header;
    @Setter
    private int width;
    private final Function<T, String> valueExtractor;

}
