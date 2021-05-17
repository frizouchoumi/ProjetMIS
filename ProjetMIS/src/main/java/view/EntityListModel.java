/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Charlotte
 */
public class EntityListModel<T> extends AbstractListModel {
    
    private List<T> entities;
    
    public EntityListModel(List<T> entities){
        if( entities == null ){
            entities = new ArrayList();
        }
        this.entities = entities;
    }
    
    public void setList(List<T> entities){
        this.entities = entities;
    }
    
    public List<T> getList(){
        return entities;
    }

    @Override
    public int getSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getElementAt(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
