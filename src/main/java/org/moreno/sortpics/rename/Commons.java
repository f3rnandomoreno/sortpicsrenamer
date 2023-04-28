package org.moreno.sortpics.rename;

import org.moreno.sortpics.rename.action.AppActionsHistorical;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class Commons {

    // size of image thumbnail visualization
    public static final int ImageW = 250;
    public static final int ImageH = 250;

    public static final int CAMERA1 = 0;
    public static final int CAMERA2 = 1;
    public static final int OP_ADD = 1;
    public static final int OP_SUBTRACT = 2;

    public static final String LANG_ES = "es";
    public static final String LANG_EN = "en";
    public static final String ALL_AUTHORS = "_# ALL PICTURES";
    public static final String NO_FERFORMAT = "_# NOT RENAMED PICTURES";
    // values "es, en"
    public static String lang = LANG_ES;
    public static String PRE_ADDED_TIMECAMERA1 = "Added time to Camera1:";
    public static String PRE_ADDED_TIMECAMERA2 = "Added time to Camera2:";
    public static String PRE_ORIGINAL_DATE = "Fecha Original: ";
    public static String PRE_CURRENT_DATE = "Fecha Actual: ";
    public static String PRE_WITH_ADDED_DATE = "Con Tiempo A�adido: ";
    public static String PRE_DIFF_DATE = "Diferencia Actual-Original: ";
    public static String NO_FOLDER_CREATION = "No Folder Creation";
    public static String FOLDER_BY_DAY = "Folder By Day";
    public static String FOLDER_BY_MONTH = "Folder By Month";
    public static String FOLDER_BY_YEAR = "Folder By Year";
    public static String PRE_ADDED_TIMECamera = "Added Camera:";
    public static String RENAME = "Rename";
    public static String COPY = "Copy";
    // Se comprueba si el autor es correcto para el archivo indicado
    public static String MOVE = "Move";

    public static AppActionsHistorical historical = new AppActionsHistorical();


    // return the allowed formats for images and videos
    public static List<String> getAllowedFormats() {
        return Stream.concat(getAllowedImageFormats().stream(),
                List.of(".mp4", ".mpg", ".mpeg", ".avi", ".mov", ".asf", ".3gp")
                        .stream()).toList();
    }

    public static List<String> getAllowedImageFormats() {
        return List.of(".jpeg", ".jpg", ".png");
    }

    public static void setLang(String lang) {
        Commons.lang = lang;
        if (lang.equals(LANG_EN)) {
            PRE_ADDED_TIMECAMERA1 = "Added to Camera1:";
            PRE_ADDED_TIMECAMERA2 = "Added to Camera2:";
            PRE_ORIGINAL_DATE = "Original Date: ";
            PRE_CURRENT_DATE = "Current Date: ";
            PRE_WITH_ADDED_DATE = "With Added Date: ";
            PRE_DIFF_DATE = "Diff Original-Current: ";
            NO_FOLDER_CREATION = "No Folder Creation";
            FOLDER_BY_DAY = "Folder By Day";
            FOLDER_BY_MONTH = "Folder By Month";
            FOLDER_BY_YEAR = "Folder By Year";
            PRE_ADDED_TIMECamera = "Added Camera:";
            RENAME = "Rename";
            COPY = "Copy";
            MOVE = "Move";
        }
        if (lang.equals(LANG_ES)) {
            PRE_ADDED_TIMECAMERA1 = "A�adido a la C�mara1:";
            PRE_ADDED_TIMECAMERA2 = "A�adido a la C�mara2:";
            PRE_ORIGINAL_DATE = "Fecha Original: ";
            PRE_CURRENT_DATE = "Fecha Actual: ";
            PRE_WITH_ADDED_DATE = "Con Tiempo A�adido: ";
            PRE_DIFF_DATE = "Dif. Fecha Actual-Original: ";
            NO_FOLDER_CREATION = "No crear carpetas";
            FOLDER_BY_DAY = "Carpetas por D�as";
            FOLDER_BY_MONTH = "Carpetas por Meses";
            FOLDER_BY_YEAR = "Carpetas por A�os";
            PRE_ADDED_TIMECamera = "Incremento C�mara:";
            RENAME = "Renombrar";
            COPY = "Copiar";
            MOVE = "Mover";
        }

    }

//	public static AppActionsHistorical doRenamingSimulation(
//			String currentPath,
//			String currentAuthor,
//			String NoExifType) throws IOException
//	{
//		
//		//variables
//		AppActionsHistorical simulation = new AppActionsHistorical();
//		
//		// Author Name escritor por el usuario
//		String authorName = "";
//		
//		// Count of progress bar
//		int countProgress = 0;
//		
//		// Name new file
//		String newFileName=null;
//		
//		// Path of each file from the currentPath
//		String path = null;
//		
//		// New Path
//		String newPath = null;
//		
//		// Original File
//		File oldFile;
//		
//		// New File
//		File newFile;
//		
//		// 
//		FileExifrInfo fileExifr = null;
//		
//		// Date of each file from the currentPath
//		String date = null;
//		
//		// Directory to explore
//		QDir dir = new QDir(currentPath);
//		
//		// set max progress bar 
//		maxProgres.emit(dir.count());
//		
//		// Dir Iterator
//		QDirIterator dirIt = new QDirIterator(currentPath, Commons.getAllowedFormats(),new Filters(Filter.NoFilter), new QDirIterator.IteratorFlags(QDirIterator.IteratorFlag.Subdirectories));
//		
//		// To operate with Dates
//		DateOperable dateOp = null;
//		
//		
//		// Creamos el historico para el renombrado
//		Date dateBulk = simulation.createBulk();
//		
//		// For each file in the currentPath
//		while(dirIt.hasNext()){
//			
//			try{
//				/////////////////////////////////////////
//				// WE PROCESS THE CURRENT FILE INSIDE TRY
//				/////////////////////////////////////////
//				
//				// get the current path file to format it
//				path = dirIt.next();
//		
//				
//				// Si el autor
//				if(Commons.checkAuthor(path, currentAuthor)){
//					
//					// obtenemos la informaci�n que hay entre par�ntesis del nombre del fichero formateado
//					String data = FerFormatName.getData(path);
//				
//					
//					// Get File Exifr Information
//					fileExifr = JpegFileMetadata.getExifrInfo(path);
//					
//					// si el autor no est� en el nombre generamos uno nuevo
//					// obtenemos un autor
//					String author = FerFormatName.getNewAuthorFromExif(authorName, fileExifr,NoExifType);
//
//
//					// get the current File through the path got it before,to format it
//					oldFile = new File(path);	
//
//					/**
//					 * Obtenemos la fecha
//					 * */
//					
//					// Get date from exif information of jpg
//					Date dateCapture = fileExifr.getCaptureDate();
//					
//					// if file has not capture date from EXIF information
//					if(dateCapture == null){
//						
//						// if dateCapture is in the file
//						if(NoExifType.equals(Commons.DATE_LAST_MODIFIED)){
//							
//							dateOp = new DateOperable(JpegFileMetadata.getLastModifiedDate(oldFile));		
//
//						}else{
//							dateOp = new DateOperable(JpegFileMetadata.getDateCreated(oldFile));
//
//						}
//						
//						
//						// Get date from file Name
//						date = JpegFileMetadata.getFileNameFormat(dateOp);
//						
//					}else{
//						
//						
//						// if dateCapture is in the file
//						dateOp = new DateOperable(dateCapture);
//						
//						// Get date from file Name
//						date = JpegFileMetadata.getFileNameFormat(dateOp);
//					}
//					
//					// si el date es null no se renombra
//					if(date!=null){
//					
//						newFileName = FerFormatName.getName(date, author, countProgress, data, FilenameUtils.getExtension(path));
//						
//						// Progress bar increment 
//						progres.emit(countProgress++);
//						
//						if(currentPath.charAt(currentPath.length()-1)==QDir.separator()){
//							// Full path of the new File
//							newPath = currentPath + newFileName;														
//						}else{
//							// Full path of the new File
//							newPath = currentPath + QDir.separator() +newFileName;																					
//						}
//												
//						// Get the File from the newPath
//						newFile = new File(FilenameUtils.normalize(newPath));
//						
//						// Move Old file to New Path (Or rename, it is the same) ** SIMULATION **
//						//FileUtils.moveFile(oldFile, newFile);	
//						
//						// guardado accion en el historial
//						simulation.addAction(new AppAction(AppAction.RENAME_FILE, path, newPath), dateBulk);
//						
//						
//					}
//					
//				}
//			}catch (Exception e) {
//				e.printStackTrace();
//				
//				
//			}
//		}
//		
//		// Set progress bar to maximum
//		progres.emit(dir.count());
//		
//		//termina el bulk del historial de renombrado
//		simulation.finishBulk();
//		
//
//		// Show message
//		QMessageBox.information(null, "Complete", "Work done.");
//		
//		return simulation;
//	}
//	
//
//	
//	public static void doRenaming(
//			String currentPath,
//			String currentAuthor,
//			String NoExifType,
//			Signal1<Integer> progres,
//			Signal1<Integer> maxProgres,
//			boolean recursive) throws IOException
//	{
//		
//		// Author Name escritor por el usuario
//		String authorName = "";
//		
//		// Count of progress bar
//		int countProgress = 0;
//		
//		// Name new file
//		String newFileName=null;
//		
//		// Path of each file from the currentPath
//		String path = null;
//		
//		// New Path
//		String newPath = null;
//		
//		// Original File
//		File oldFile;
//		
//		// New File
//		File newFile;
//		
//		// 
//		FileExifrInfo fileExifr = null;
//		
//		// Date of each file from the currentPath
//		String date = null;
//		
//		// Directory to explore
//		QDir dir = new QDir(currentPath);
//		
//		// set max progress bar 
//		maxProgres.emit(dir.count());
//		QDirIterator dirIt = null;
//		
//		// Dir Iterator
//		if(recursive==true){
//			dirIt = new QDirIterator(currentPath, Commons.getAllowedFormats(),new Filters(Filter.NoFilter), new QDirIterator.IteratorFlags(QDirIterator.IteratorFlag.Subdirectories));
//		}else{
//			dirIt = new QDirIterator(currentPath, Commons.getAllowedFormats());			
//		}
//		
//		// To operate with Dates
//		DateOperable dateOp = null;
//		
//		
//		// Creamos el historico para el renombrado
//		Date dateBulk = historical.createBulk();
//		
//		// For each file in the currentPath
//		while(dirIt.hasNext()){
//			
//			try{
//				/////////////////////////////////////////
//				// WE PROCESS THE CURRENT FILE INSIDE TRY
//				/////////////////////////////////////////
//				
//				// get the current path file to format it
//				path = dirIt.next();
//		
//				
//				// Si el autor
//				if(Commons.checkAuthor(path, currentAuthor)){
//					
//					// obtenemos la informaci�n que hay entre par�ntesis del nombre del fichero formateado
//					String data = FerFormatName.getData(path);
//				
//					
//					// Get File Exifr Information
//					fileExifr = JpegFileMetadata.getExifrInfo(path);
//					
//					// si el autor no est� en el nombre generamos uno nuevo
//					// obtenemos un autor
//					String author = FerFormatName.getNewAuthorFromExif(authorName, fileExifr,NoExifType);
//
//
//					// get the current File through the path got it before,to format it
//					oldFile = new File(path);	
//
//					/**
//					 * Obtenemos la fecha
//					 * */
//					
//					// Get date from exif information of jpg
//					Date dateCapture = fileExifr.getCaptureDate();
//					
//					// if file has not capture date from EXIF information
//					if(dateCapture == null){
//						
//						// if dateCapture is in the file
//						if(NoExifType.equals(Commons.DATE_LAST_MODIFIED)){
//							
//							dateOp = new DateOperable(JpegFileMetadata.getLastModifiedDate(oldFile));		
//
//						}else{
//							dateOp = new DateOperable(JpegFileMetadata.getDateCreated(oldFile));
//
//						}
//						
//						
//						// Get date from file Name
//						date = JpegFileMetadata.getFileNameFormat(dateOp);
//						
//					}else{
//						
//						
//						// if dateCapture is in the file
//						dateOp = new DateOperable(dateCapture);
//						
//						// Get date from file Name
//						date = JpegFileMetadata.getFileNameFormat(dateOp);
//					}
//					
//					// si el date es null no se renombra
//					if(date!=null){
//					
//						newFileName = FerFormatName.getName(date, author, countProgress, data, FilenameUtils.getExtension(path));
//						
//						// Progress bar increment 
//						progres.emit(countProgress++);
//						
//						if(currentPath.charAt(currentPath.length()-1)==QDir.separator()){
//						// Full path of the new File
//							newPath = currentPath + newFileName;														
//						}else{
//						// Full path of the new File
//							newPath = currentPath + QDir.separator() +newFileName;																					
//						}
//						//newPath = FilenameUtils.getFullPath(path) + newFileName;														
//												
//						// Get the File from the newPath
//						newFile = new File(FilenameUtils.normalize(newPath));
//						
//						// Move Old file to New Path (Or rename, it is the same)
//						FileUtils.moveFile(oldFile, newFile);	
//						
//						// guardado accion en el historial
//						historical.addAction(new AppAction(AppAction.RENAME_FILE, path, newPath), dateBulk);
//						
//						
//					}
//					
//				}
//			}catch (Exception e) {
//				e.printStackTrace();
//				
//				
//			}
//		}
//		
//		// Set progress bar to maximum
//		progres.emit(dir.count());
//		
//		//termina el bulk del historial de renombrado
//		historical.finishBulk();
//		
//
//		// Show message
//		QMessageBox.information(null, "Complete", "Work done.");
//		
//	}
//	
//	public static void doMoveFilesToCurrentDirectory(
//			String currentPath,
//			String currentAuthor,
//			String NoExifType,
//			Signal1<Integer> progres,
//			Signal1<Integer> maxProgres) throws IOException
//	{
//		
//		// Author Name escritor por el usuario
//		String authorName = "";
//		
//		// Count of progress bar
//		int countProgress = 0;
//		
//		// Name new file
//		String newFileName=null;
//		
//		// Path of each file from the currentPath
//		String path = null;
//		
//		// New Path
//		String newPath = null;
//		
//		// Original File
//		File oldFile;
//		
//		// New File
//		File newFile;
//		
//		// 
//		FileExifrInfo fileExifr = null;
//		
//		// Date of each file from the currentPath
//		String date = null;
//		
//		// Directory to explore
//		QDir dir = new QDir(currentPath);
//		
//		// set max progress bar 
//		maxProgres.emit(dir.count());
//		QDirIterator dirIt = null;
//		
//
//		dirIt = new QDirIterator(currentPath, Commons.getAllowedFormats(),new Filters(Filter.NoFilter), new QDirIterator.IteratorFlags(QDirIterator.IteratorFlag.Subdirectories));
//		
//		
//		
//		// To operate with Dates
//		DateOperable dateOp = null;
//		
//		
//		// Creamos el historico para el renombrado
//		Date dateBulk = historical.createBulk();
//		
//		// For each file in the currentPath
//		while(dirIt.hasNext()){
//			
//			try{
//				/////////////////////////////////////////
//				// WE PROCESS THE CURRENT FILE INSIDE TRY
//				/////////////////////////////////////////
//				
//				// get the current path file to format it
//				path = dirIt.next();
//		
//				
//				// Si el autor
//				if(Commons.checkAuthor(path, currentAuthor)){
//									
//					// Get File Exifr Information
//					fileExifr = JpegFileMetadata.getExifrInfo(path);
//					
//					// get the current File through the path got it before,to format it
//					oldFile = new File(path);	
//
//					/**
//					 * Obtenemos la fecha
//					 * */
//					
//					// Get date from exif information of jpg
//					Date dateCapture = fileExifr.getCaptureDate();
//					
//					// if file has not capture date from EXIF information
//					if(dateCapture == null){
//						
//						// if dateCapture is in the file
//						if(NoExifType.equals(Commons.DATE_LAST_MODIFIED)){
//							
//							dateOp = new DateOperable(JpegFileMetadata.getLastModifiedDate(oldFile));		
//
//						}else{
//							dateOp = new DateOperable(JpegFileMetadata.getDateCreated(oldFile));
//
//						}
//						
//						
//						// Get date from file Name
//						date = JpegFileMetadata.getFileNameFormat(dateOp);
//						
//					}else{
//						
//						
//						// if dateCapture is in the file
//						dateOp = new DateOperable(dateCapture);
//						
//						// Get date from file Name
//						date = JpegFileMetadata.getFileNameFormat(dateOp);
//					}
//					
//					// si el date es null no se renombra
//					if(date!=null){
//					
//						newFileName = FilenameUtils.getName(path);
//						
//						// Progress bar increment 
//						progres.emit(countProgress++);
//						
//						newPath = currentPath + newFileName;														
//												
//						if(!path.equals(newPath)){							
//							// Get the File from the newPath
//							newFile = new File(FilenameUtils.normalize(newPath));
//							
//							// Move Old file to New Path (Or rename, it is the same)
//							FileUtils.moveFile(oldFile, newFile);	
//							
//							// guardado accion en el historial
//							historical.addAction(new AppAction(AppAction.MOVE_FILE, path, newPath), dateBulk);
//						}
//						
//						
//					}
//					
//				}
//			}catch (Exception e) {
//				e.printStackTrace();
//				
//				
//			}
//		}
//		
//		// Set progress bar to maximum
//		progres.emit(dir.count());
//		
//		//termina el bulk del historial de renombrado
//		historical.finishBulk();
//		
//
//		// Show message
//		QMessageBox.information(null, "Complete", "Work done.");
//		
//	}
//	
//	
//	public static void doRemoveEmptyDirs(
//			String currentPath,
//			Signal1<Integer> progres,
//			Signal1<Integer> maxProgres) throws IOException
//	{
//		
//		
//		// Path of each file from the currentPath
//		String path = null;
//		
//		// Directory to explore
//		QDir dir = new QDir(currentPath);
//		System.out.println("doRemoveEmptyDirs currentPath:" + currentPath);
//		
//		// set max progress bar 
//		maxProgres.emit(dir.count());
//		QDirIterator dirIt = null;
//		// Dir Iterator
//		dirIt = new QDirIterator(currentPath, Commons.getAllowedFormats(),new Filters(Filter.AllDirs));
//		//dirIt = new QDirIterator(currentPath, Commons.getAllowedFormats(),new Filters(Filter.Dirs),new QDirIterator.IteratorFlags(QDirIterator.IteratorFlag.Subdirectories));
//		
//		// Creamos el historico para el renombrado
//		Date dateBulk = historical.createBulk();
//		
//		// For each file in the currentPath
//		while(dirIt.hasNext()){
//			
//			try{
//				/////////////////////////////////////////
//				// WE PROCESS THE CURRENT FILE INSIDE TRY
//				/////////////////////////////////////////
//				// get the current path file to format it
//				path = dirIt.next();
//				System.out.println("doRemoveEmptyDirs:" + path);
//				if(path.equals("G:/Fotos y Videos y Audios/Por fecha/bluethoot")){
//					System.out.println("paro aqui");
//				}
//				if(dir.rmdir(path)==true){
//					// guardado accion en el historial
//					historical.addAction(new AppAction(AppAction.REMOVED_DIR, path, ""), dateBulk);
//				}
//					
//				
//			}catch (Exception e) {
//				e.printStackTrace();
//				
//				
//			}
//		}
//		
//		// Set progress bar to maximum
//		progres.emit(dir.count());
//		
//		//termina el bulk del historial de renombrado
//		historical.finishBulk();
//		
//
//		// Show message
//		QMessageBox.information(null, "Complete", "Work done.");
//		
//	}
}
