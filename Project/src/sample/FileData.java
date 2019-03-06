package sample;

public class FileData {

    private String fullPath;
    private IsFileChanged isChanged;
    private Integer size;

    public FileData(String fullPath, IsFileChanged isChanged, Integer size) {
        this.fullPath = fullPath;
        this.isChanged = isChanged;
        this.size = size;
    }

    //these getters are used to show details in table view
    public String getFullPath() {
        return fullPath;
    }

    public IsFileChanged getIsChanged() {
        return isChanged;
    }

    public Integer getSize() {
        return size;
    }
}
