package jp.sourceforge.gokigen.memoma;


/**
 *   �I�u�W�F�N�g���I�����ꂽ���Ƃ�ʒm����
 * 
 * @author MRSa
 *
 */
public interface IObjectSelectionReceiver
{
	public abstract int touchedVacantArea();
	public abstract int touchUppedVacantArea();
	public abstract void objectCreated();
    public abstract boolean objectSelected(Integer key);
    public abstract void objectSelectedContext(Integer key);
	
}
