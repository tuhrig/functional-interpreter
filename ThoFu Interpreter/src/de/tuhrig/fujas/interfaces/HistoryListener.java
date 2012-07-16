package de.tuhrig.fujas.interfaces;

import java.util.Date;

import de.tuhrig.fujas.types.LList;

public interface HistoryListener {

	public void update(LList tokens, Date started, Date ended);
}