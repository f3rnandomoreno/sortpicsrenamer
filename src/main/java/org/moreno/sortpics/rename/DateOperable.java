/*   Copyright 2010 Fernando Moreno Ruiz All Rights Reserved.
 *
 * 	 This file is part of FotoMixDate.
 *
 *   FotoMixDate is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   FotoMixDate is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with FotoMixDate.  If not, see <http://www.gnu.org/licenses/>.
 * */
package org.moreno.sortpics.rename;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Fernando Moreno Ruiz <fernandomorenoruiz@gmail.com>
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateOperable extends Date{
    /**
     *
     */
    private boolean _isExif = false;
    private static final long serialVersionUID = 1L;
    public DateOperable(Date date) {
        super(date.getTime());
    }
    public DateOperable(Date date, boolean isExif) {
        super(date.getTime());
        setIsExif(isExif);
    }
    public void addSeconds(long seconds){
        setTime(this.getTime()+ seconds*1000);
    }
    public void subtractSeconds(long seconds){
        setTime(this.getTime()- seconds*1000);
    }
    public int absSecondsDifference(DateOperable d){
        return Math.abs((int) ((this.getTime() - d.getTime()) / 1000));
    }
    public long getSecondsDifference(DateOperable d) {
        return (this.getTime()-d.getTime()) / 1000;
    }
    public boolean getIsExif() {
        return _isExif;
    }
    public void setIsExif(boolean _isExif) {
        this._isExif = _isExif;
    }
    public String getYearString(){
        return new SimpleDateFormat("yyyy").format(this);
    }
    public String getMonthString(){
        return new SimpleDateFormat("MM").format(this);
    }
    public String getDayString(){
        return new SimpleDateFormat("dd").format(this);
    }
    public String getDateString(){
        return new SimpleDateFormat("dd-MM-yyyy").format(this);
    }
    public String getTimeString(){
        return new SimpleDateFormat("HH:mm:ss").format(this);
    }

}
