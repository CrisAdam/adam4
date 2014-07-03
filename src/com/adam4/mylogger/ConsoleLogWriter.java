package com.adam4.mylogger;


public class ConsoleLogWriter  implements iLogWriter
{

	@Override
	public void close()
	{
		// probably not needed...
		System.out.println("Ending console logger");
		//System.out.close();
	}

	@Override
	public boolean addError(Error error)
	{
		System.out.println(error);
		return true;
	}

	@Override
	public void reconnect(ReAdd reAdd)
	{
		// System.out.println should not fail
		reAdd.add(this);
	}

}
