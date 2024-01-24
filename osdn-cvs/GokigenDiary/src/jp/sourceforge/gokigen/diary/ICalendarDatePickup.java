package jp.sourceforge.gokigen.diary;

public interface ICalendarDatePickup
{
    public abstract void decideDate(int year, int month, int day);
    
    public abstract boolean setAppendCharacter(int year, int month, char[] appendChar);
}
