package com.adam4.spacenet;

public interface ISpaceNetCommand
{
    public default ISpaceNetCommand getCommand()
    {
        return null;

    }

    public int getMax();

    public void process(String prefix, String[] arguments, String trailing);

    /*
     * } NICK(1, 1) {
     * 
     * @Override public void run(Connection con, String prefix, String[] arguments) throws Exception { if (con.nick == null) doFirstTimeNick(con, arguments[0]); else doSelfSwitchNick(con, arguments[0]); }
     * 
     * private void doSelfSwitchNick(Connection con, String nick) { synchronized (mutex) { String oldNick = con.nick; con.nick = filterAllowedNick(nick); connectionMap.remove(oldNick); connectionMap.put(con.nick, con); con.send(":" +
     * oldNick + "!" + con.username + "@" + con.hostname + " NICK :" + con.nick);
     * 
     * for (Channel c : channelMap.values()) { if (c.channelMembers.contains(con)) c .sendNot(con, ":" + oldNick + "!" + con.username + "@" + con.hostname + " NICK :" + con.nick); } } }
     * 
     * private Command(int min, int max) { minArgumentCount = min; maxArgumentCount = max; }
     * 
     * public int getMin() { return minArgumentCount; }
     * 
     * public int getMax() { return maxArgumentCount; }
     * 
     * public abstract void run(Connection con, String prefix, String[] arguments) throws Exception;
     */
}
