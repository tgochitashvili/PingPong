package com.tgoch;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Readers{
    public static LinkedList<ProcessPool> getProcessPoolsTxt(String urlPath, String serverPath, String token){
        LinkedList<ProcessPool> processList = new LinkedList<ProcessPool>();
        ProcessPool processPool = null;
        Scanner urlScnr = null;
        Scanner serverScnr = null;
        try{
            File urlFile = new File(urlPath);
            urlScnr = new Scanner(urlFile);
            File serverFile = new File(serverPath);
            serverScnr = new Scanner(serverFile);
            LinkedList<String> servers = new LinkedList<String>();
            LinkedList<String> urls = new LinkedList<String>();
            while(serverScnr.hasNextLine()){
                String tempStr = serverScnr.nextLine();
                servers.add(tempStr);
            }
            while(urlScnr.hasNextLine()){
                urls.add(urlScnr.nextLine());
            }
            for(String server: servers){
                processPool = new ProcessPool(server);
                for(String url: urls){
                    if(!url.equals("")){
                        String tempUrl = url.replaceAll(token,server);
                        processPool.processList.add(new Process(tempUrl));
                    }
                }
                processList.add(processPool);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(urlScnr != null){
                urlScnr.close();
            }
            if(serverScnr != null){
                serverScnr.close();
            }
        }
        return processList;
    }
    public static LinkedList<ProcessPool> getProcessPoolsTxt(String urlPath){
        LinkedList<ProcessPool> processList = null;
        ProcessPool processPool = null;
        Scanner scnr = null;
        try{
            File urlFile = new File(urlPath);
            scnr = new Scanner(urlFile);
            LinkedList<String> urls = new LinkedList<String>();
            while(scnr.hasNextLine()){
                urls.add(scnr.nextLine());
            }
            processPool = new ProcessPool();
            for(String url: urls){
                if(!url.equals(""))
                    processPool.processList.add(new Process(url));
            }
            processList = new LinkedList<ProcessPool>();
            processList.add(processPool);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if(scnr != null){
                scnr.close();
            }
        }
        return processList;
    }

    public LinkedList<ProcessPool> getProcessPoolsXslx(String urlPath, String urlSheetName, String serverSheetName) throws IOException{
        File file = null;
        FileInputStream fInputStream = null;
        XSSFWorkbook workbook = null;
        try{
            file = new File(urlPath);
            fInputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(fInputStream);
            XSSFSheet serverSheet = workbook.getSheet(urlSheetName);
            XSSFSheet urlSheet = workbook.getSheet(serverSheetName);
            Iterator<Row> serverItr = serverSheet.rowIterator();
            Iterator<Row> urlItr = urlSheet.rowIterator();
            LinkedList<String> serverRowList = new LinkedList<String>();
            LinkedList<String> urlRowList = new LinkedList<String>();
            while(serverItr.hasNext()){
                serverRowList.add(serverItr.next().getCell(0).getStringCellValue());
            }
            while(urlItr.hasNext()){
                urlRowList.add(urlItr.next().getCell(0).getStringCellValue());
            }

            return null;
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
        finally{
            if(workbook != null){
                workbook.close();
            } else if (fInputStream != null){
                fInputStream.close();
            }
        }
    }
}
