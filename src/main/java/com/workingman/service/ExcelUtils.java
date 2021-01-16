package com.workingman.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    /**
     * 获取excel的一列，从0开始，到null处结束
     * @param sheet：表格
     * @param column：所要获取的列
     * @return Cell的集合
     */
    public static List<Cell> getColumn(Sheet sheet,int column){
        List<Cell> cells=new ArrayList<>();
        Row row=null;
        Cell cell=null;
        for(int i=0;;i++){
            row=sheet.getRow(i);
            if(row==null){
                break;
            }
            cell=row.getCell(column);
            if(cell==null){
                break;
            }
            cells.add(cell);
        }
        return cells;
    }

    /**
     * 获取excel的一列，从index行开始，到null处结束
     * @param sheet：表格
     * @param column：所要获取的列
     * @param index：获取的开始行数
     * @return Cell的集合
     */
    public static List<Cell> getColumn(Sheet sheet,int column,int index){
        List<Cell> cells=new ArrayList<>();
        Row row=null;
        Cell cell=null;
        for(int i=index;;i++){
            row=sheet.getRow(i);
            if(row==null){
                break;
            }
            cell=row.getCell(column);
            if(cell==null){
                break;
            }
            cells.add(cell);
        }
        return cells;
    }

    /**
     * 获取excel的一列，从index行开始，到end行结束
     * @param sheet：表格
     * @param column：所要获取的列
     * @param index：获取的开始行数
     * @param end：获取的结束行数
     * @return Cell的集合
     */
    public static List<Cell> getColumn(Sheet sheet,int column,int index,int end){
        List<Cell> cells=new ArrayList<>();
        Row row=null;
        Cell cell=null;
        for(int i=index;i<=end;i++){
            row=sheet.getRow(i);
            if(row==null){
                break;
            }
            cell=row.getCell(column);
            cells.add(cell);
        }
        return cells;
    }
}
