package org.moreno.sortpics.rename.action;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class AppAction {

    static public final int MOVE_FILE = 0;
    static public final int RENAME_FILE = 1;
    static public final int REMOVED_DIR = 2;
    private int _type = 0;
    private String _sourceFile = "";
    private String _destinyFile = "";
    private Date _dateAction = null;
    private boolean _error = false;
    private String _errorMessage = "";

    public AppAction(int type, String sourceFile, String destinyFile) {
        _type = type;
        _sourceFile = sourceFile;
        _destinyFile = destinyFile;
        _dateAction = new Date();
    }

    public void set_destinyFile(String _destinyFile) {
        this._destinyFile = _destinyFile;
    }

    public void set_sourceFile(String _sourceFile) {
        this._sourceFile = _sourceFile;
    }

    public void setType(int type) {
        this._type = type;
    }

    public void set_errorMessage(String _errorMessage) {
        this._errorMessage = _errorMessage;
    }

    public String get_destinyFile() {
        return _destinyFile;
    }

    public String get_sourceFile() {
        return _sourceFile;
    }

    public String get_errorMessage() {
        return _errorMessage;
    }

    public String get_sourceFolder() {
        return FilenameUtils.getFullPath(_sourceFile);
    }

    public String get_destinyFolder() {
        return FilenameUtils.getFullPath(_destinyFile);
    }

    public int getType() {
        return _type;
    }

    public Date get_dateAction() {
        return _dateAction;
    }

    /**
     * Lleva a cabo la acci�n
     */
    public void doAction() {
        switch (_type) {
            case MOVE_FILE: // TODO move file
//				String newFile = "";
//				if(_destinyFile.charAt(_destinyFile.length()-1)==QDir.separator()){
//					newFile = _destinyFile+FilenameUtils.getName(_sourceFile);				
//				}else{
//					newFile = _destinyFile+QDir.separator()+FilenameUtils.getName(_sourceFile);
//				}
//				try {
//					FileUtils.moveFile(new File(_sourceFile),new File(newFile));
//				} catch (IOException e) {
//					_error = true;
//					_errorMessage = e.getMessage();
//					
//				}
                break;
            case RENAME_FILE:
                try {
                    FileUtils.moveFile(new File(_sourceFile), new File(_destinyFile));
                } catch (IOException e) {
                    _error = true;
                    _errorMessage = e.getMessage();
                }
                break;
            case REMOVED_DIR: // TODO remove dir
//				QDir dir = new QDir();
//				_error = !dir.rmdir(_sourceFile);
//				if(_error==true){
//					if(dir.exists(_sourceFile)){
//						_errorMessage = "La carpeta no est� vac�a.";
//					}else{
//						_errorMessage = "La carpeta no existe.";
//					}
//				}
                break;
        }
    }

    public void undo() throws IOException {

        switch (_type) {
            case MOVE_FILE: // TODO move file
//				String newFile = "";
//				if(_destinyFile.charAt(_destinyFile.length()-1)==QDir.separator()){
//					newFile = _destinyFile+FilenameUtils.getName(_sourceFile);				
//				}else{
//					newFile = _destinyFile+QDir.separator()+FilenameUtils.getName(_sourceFile);
//				}
//				FileUtils.moveFile(new File(newFile),new File(_sourceFile));
                break;
            case RENAME_FILE:
                FileUtils.moveFile(new File(_destinyFile), new File(_sourceFile));
                break;
            case REMOVED_DIR: // TODO remove dir
//				QDir dir = new QDir();
//				dir.mkdir(_sourceFile);
                break;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        switch (_type) {
            case MOVE_FILE:
                return "mover_fichero:[" + _sourceFile + "->" + _destinyFile + "]";
            case RENAME_FILE:
                return "renombrar_fichero:[" + _sourceFile + "->" + _destinyFile + "]";
            case REMOVED_DIR:
                return "eliminado_directorio:[" + _sourceFile + "]";
        }
        return "";
    }
}
