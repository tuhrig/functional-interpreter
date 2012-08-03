package de.tuhrig.thofu.interfaces;

import java.util.Date;

import de.tuhrig.thofu.types.LList;

/**
 * An interface implemented by classes that store
 * the executed commands.
 * 
 * @author Thomas Uhrig (tuhrig.de)
 */
public interface HistoryListener {

	public void update(LList tokens, Date started, Date ended);
}