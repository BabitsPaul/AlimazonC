package warehouse;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * just a tableModel to manage the orderTable's functionality
 * 
 * @author stefan
 *
 */
public class OrderTableModel implements TableModel {

	public OrderTableModel(Order o)
	{
		this.order=o;
	}
	
	private Order order;
	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}
	
	public void setOrder(Order o)
	{
		this.order=o;
	}
	

	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int colNr) {
		
		switch (colNr) {
		case 0:
			return "product";
		case 1:
			return "count";
		case 2:
			return "status";
		case 4: 
			return "skip";
		default:
			return "";//should actually never happen
		}
	}

	@Override
	public int getRowCount() {
		
		return order.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		
		switch (col) {
		case 0:
			return order.get(row).product.name;
		case 1:
			return order.get(row).count;
		case 2:
			return order.get(row).done?"DONE":"OPEN";
		case 3:
			return "skip";

		default:
			return "";
		}
		

	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void manageEvent(int row, int col, JTable orderTable) {

		
		if(row>-1&&row<orderTable.getRowCount()&&col>-1&&col<orderTable.getColumnCount())
		{
			if(col==2)
				order.switchState(row);
			else if(col==3)
				order.moveDown(row);
			
			orderTable.repaint();
		}

		
	}

}
