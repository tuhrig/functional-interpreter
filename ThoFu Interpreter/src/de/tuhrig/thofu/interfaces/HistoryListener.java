package de.tuhrig.thofu.interfaces;

import java.util.Date;

import de.tuhrig.thofu.types.LList;

public interface HistoryListener {

	public void update(LList tokens, Date started, Date ended);
}