package com.wishbook.catalog.home.more.adapters.treeview.bean;


import com.wishbook.catalog.home.more.adapters.treeview.treerecyclerview.annotation.NodeId;
import com.wishbook.catalog.home.more.adapters.treeview.treerecyclerview.annotation.NodePid;

/**
 * Created by ID_MARR on 2015/4/26.
 */
public class FileBean {

    @NodeId
    public int fileId;
    @NodePid
    public int filePId;
    public boolean isChecked=false;

    public String fileName;

    public FileBean(int fileId, int filePId, String fileName) {
        this.fileId = fileId;
        this.filePId = filePId;
        this.fileName = fileName;
        this.isChecked=false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getFilePId() {
        return filePId;
    }

    public void setFilePId(int filePId) {
        this.filePId = filePId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
