import java.util.List;
import java.io.File;

public interface Reader {
    public List<ProcessNode> getProcessNodesFromSource(File file);
}