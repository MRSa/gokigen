package jp.sourceforge.gokigen.aligner;

public interface ICalendarDatePickup
{
    public abstract void decideDate(int year, int month, int day);
    
    public abstract boolean setAppendCharacter(int year, int month, char[] appendChar);
}
