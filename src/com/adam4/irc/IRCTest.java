package com.adam4.irc;

import static org.junit.Assert.*;

import org.junit.Test;

public class IRCTest
{

    @Test
    public void testParseLine()
    {
        @SuppressWarnings("unused")
        String[] test0 = { ":127.0.0.1.mydomain.com 001 myNick :Welcome to the server", "PING :25BD9D87", "PONG :25BD9D87", "JOIN #chan", };
        String[] test = {
                "NICK note2",
                "USER note2 8 * :real name",
                ":ip-112-21-15-51.us-west-1.compute.internal NOTICE AUTH :*** Looking up your hostname...",
                ":ip-112-21-15-51.us-west-1.compute.internal NOTICE AUTH :*** Found your hostname",
                "PING :25CD9D87",
                "PONG :25CD9D87",
                ":ip-112-21-15-51.us-west-1.compute.internal 001 note2 :Welcome to the 263TestIRC IRC Network note2!note2@c-18-72-16-216.hsd1.mi.comcast.net",
                ":ip-112-21-15-51.us-west-1.compute.internal 002 note2 :Your host is ip-112-21-15-51.us-west-1.compute.internal, running version Unreal3.2.8.1",
                ":ip-112-21-15-51.us-west-1.compute.internal 003 note2 :This server was created Wed Jul 9 2014 at 11:58:09 EDT",
                ":ip-112-21-15-51.us-west-1.compute.internal 004 note2 ip-112-21-15-51.us-west-1.compute.internal Unreal3.2.8.1 iowghraAsORTVSxNCWqBzvdHtGp lvhopsmntikrRcaqOALQbSeIKVfMCuzNTGj",
                ":ip-112-21-15-51.us-west-1.compute.internal 005 note2 UHNAMES NAMESX SAFELIST HCN MAXCHANNELS=30 CHANLIMIT=#:30 MAXLIST=b:60,e:60,I:60 NICKLEN=30 CHANNELLEN=32 TOPICLEN=307 KICKLEN=307 AWAYLEN=307 MAXTARGETS=20 :are supported by this server",
                ":ip-112-21-15-51.us-west-1.compute.internal 005 note2 WALLCHOPS WATCH=128 WATCHOPTS=A SILENCE=15 MODES=12 CHANTYPES=# PREFIX=(qaohv)~&@%+ CHANMODES=beI,kfL,lj,psmntirRcOAQKVCuzNSMTG NETWORK=263TestIRC CASEMAPPING=ascii EXTBAN=~,cqnr ELIST=MNUCT STATUSMSG=~&@%+ :are supported by this server",
                ":ip-112-21-15-51.us-west-1.compute.internal 005 note2 EXCEPTS INVEX CMDS=KNOCK,MAP,DCCALLOW,USERIP :are supported by this server",
                ":ip-112-21-15-51.us-west-1.compute.internal 251 note2 :There are 1 users and 0 invisible on 1 servers", ":ip-112-21-15-51.us-west-1.compute.internal 255 note2 :I have 1 clients and 0 servers",
                ":ip-112-21-15-51.us-west-1.compute.internal 265 note2 :Current Local Users: 1  Max: 3", ":ip-112-21-15-51.us-west-1.compute.internal 266 note2 :Current Global Users: 1  Max: 3",
                ":ip-112-21-15-51.us-west-1.compute.internal 422 note2 :MOTD File is missing", ":note2 MODE note2 :+ix", "JOIN #chan", ":note2!note2@HiddenPrefix-F0C40450.hsd1.mi.comcast.net JOIN :#chan",
                ":ip-112-21-15-51.us-west-1.compute.internal 353 note2 = #chan :@note2 ", ":ip-112-21-15-51.us-west-1.compute.internal 366 note2 #chan :End of /NAMES list.", "JOIN #test",
                ":note2!note2@HiddenPrefix-F0C40450.hsd1.mi.comcast.net JOIN :#test", ":ip-112-21-15-51.us-west-1.compute.internal 353 note2 = #test :@note2 ",
                ":ip-112-21-15-51.us-west-1.compute.internal 366 note2 #test :End of /NAMES list.", "PRIVMSG #test :hi", "QUIT :bye", "ERROR :Closing Link: note2[c-18-72-16-216.hsd1.mi.comcast.net] (Quit: bye)", };

        for (String s : test)
        {
            System.out.println(IRC.parseLine(s));
        }
    }

}
