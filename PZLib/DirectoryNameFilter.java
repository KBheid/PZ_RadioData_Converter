import javax.swing.filechooser.FileFilter;
import java.io.File;

public class DirectoryNameFilter extends FileFilter {
	private String filename;

	DirectoryNameFilter(String filename) { this.filename = filename; }

	@Override
	public boolean accept(File f) {
		return f.isDirectory();
	}

	@Override
	public String getDescription() {
		return filename + " Directory";
	}
}

