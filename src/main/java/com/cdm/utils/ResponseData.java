package com.cdm.utils;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {

    private int count;

    private List<GlobalInfo> entries;
}