package org.moreno.sortpics.rename.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
public class AppActionsHistorical {

    private LinkedHashMap<Date, List<AppAction>> _actions;


    public AppActionsHistorical() {
        _actions = new LinkedHashMap<Date, List<AppAction>>();

    }

    public Date createBulk() {
        Date date = new Date();
        _actions.put(date, new LinkedList<>());
        return date;
    }

    public void addAction(AppAction act, Date date) {
        _actions.get(date).add(act);
    }

    /**
     * A�ade una lista de acciones en una fecha determinada
     *
     * @param act  Acciones que se agregan
     * @param date La fecha en las que se hicieron las acciones
     */
    public void addActions(List<AppAction> act, Date date) {
        for (AppAction a : act) {
            _actions.get(date).add(a);
        }

    }


    public List<AppAction> getActions(Date date) {
        return _actions.get(date);
    }

    public List<AppAction> getLastActions() {

        if (_actions.size() > 0) {
            Object[] a = _actions.values().toArray();
            return (List<AppAction>) a[a.length - 1];
        }

        return null;
    }

    public List<Date> getDates() {
        Set<Date> dates = _actions.keySet();
        List<Date> datesSorted = new LinkedList<>();
        datesSorted.addAll(dates);
        Collections.sort(datesSorted);
        return datesSorted;
    }

    public List<String> getDirectoriesBulk(Date date) {
        List<String> directories = new ArrayList<>();
        List<AppAction> actions = getActions(date);
        for (AppAction a : actions) {
            if (!directories.contains(a.get_sourceFolder())) {
                directories.add(a.get_sourceFolder());
            }
        }
        return directories;
    }


    /**
     * Deshace las �ltimas acciones realizadas
     *
     * @throws IOException
     */
    public void undo() throws IOException {
        List<AppAction> actions = this.getLastActions();
        for (AppAction act : actions) {
            act.undo();
        }
        this.removeLastActions();
    }

    /**
     * Elimina las �ltimas acciones realizadas
     */
    private void removeLastActions() {
        Object[] keys = _actions.keySet().toArray();
        if (keys.length > 0) {
            _actions.remove(keys[keys.length - 1]);
        }
    }

    public boolean isEmpty() {
        return _actions.isEmpty();
    }


    /**
     * @return genera un �rbol donde est�n las carpetas y las im�genes ya renombradas
     */
//	public List<QTreeWidgetItem> listTreeItems(){
//		List<AppAction> list = getLastActions();
//		List<String> checkFolders = new ArrayList<>();
//		List<QTreeWidgetItem> folders = new ArrayList<QTreeWidgetItem>();
//		List<QTreeWidgetItem> files = new ArrayList<QTreeWidgetItem>();
//		QTreeWidgetItem item = null;
//		for(AppAction act: list){
//			String path = FilenameUtils.getFullPath(act.get_destinyFile());
//			if(!checkFolders.contains(path)){
//				checkFolders.add(path);
//				List<String> lst = new ArrayList<String>();
//	            lst.add(path);
//	            item = new QTreeWidgetItem((QTreeWidgetItem) null, lst);
//	            item.setIcon(0, new QIcon(new QPixmap("classpath:images/folder.png")));
//	            folders.add(item);	            
//			}
//			String filename = FilenameUtils.getBaseName(act.get_destinyFile());
//			List<String> listName = new ArrayList<String>();
//			listName.add(filename);
//			QTreeWidgetItem itemFile = new QTreeWidgetItem(listName);
////			ImageToDraw image = new ImageToDraw(act.get_sourceFile(), new QSize(100,100));
////			itemFile.setIcon(0, new QIcon(image.getQPixmap()));
//			item.addChild(itemFile);
//			
//		}
//		return folders;
//		
//	}


}
