import java.io.*;
import com.opencsv.CSVWriter;
public class LogWriter {
	File file;
	FileWriter outFile; 
	CSVWriter csvFile;
	LogWriter(String path){
		try{
			this.file = new File(path);
			this.outFile = new FileWriter(file);
			this.csvFile = new CSVWriter(outFile);
			String[] header  = {"Cycle", "ID", "Type", "Activity"};
			csvFile.writeNext(header);
		}
		catch (IOException e){
                        e.printStackTrace();
                }
	}
	public void writeLog(String[] cycleLog){
		csvFile.writeNext(cycleLog);
	}

	public void close(){
		try {
			csvFile.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
