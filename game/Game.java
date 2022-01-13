package game;

import java.util.LinkedList;

public class Game
{    
    final int NUM_INNINGS = 2;
    private int inning;
    boolean isTopOfInning;
    private Count count;
    private Team homeTeam;
    private Team awayTeam;
    private Team battingTeam;
    private Team pitchingTeam;
    private int outs;
    private LinkedList <Integer> runners;
    String message;
    public boolean isGameOver = false;
    private int namePadding;
    int currentInningRuns;
    
    public Game()
    {
        inning = 1;
        isTopOfInning = true;
        count = new Count();
        outs = 0;
        awayTeam = new Team("AWAY");
        homeTeam = new Team("HOME");
        battingTeam = awayTeam;
        pitchingTeam = homeTeam;
        message = "Play Ball!";
        runners = new LinkedList<Integer>();
        namePadding = Math.max(awayTeam.name.length(), homeTeam.name.length());
        battingTeam.inningsRunsScored.add(0);
    }
    
    public void callBall()
    {
        count.balls++;
        message = "Ball";      
        if (count.checkWalk())
        {
            message = "Walk";
            advanceRunnersWalk();
        }
    }
    
    public void callStrike()
    {
        count.strikes++;
        message = "Strike";      
        if (count.checkStrikeout())
        {
            message = "Struckout";
            outMade();
        }
    }
    
    public void advanceRunnersHit(int numBases)
    {
        switch (numBases)
        {
            case 1:
            message = "Single";
                break;
                case 2:
                message = "Double";
                break;
                case 3:
                message = "Triple";
                break;
                case 4:
                message = (runners.size() != 3 ? "Homerun" : "Grand Slam");
                break;
            }
            battingTeam.hit();
        for (int i = 0; i < runners.size(); i++)
        {
            runners.set(i, runners.get(i) +numBases);
        }
        runners.addFirst(numBases);
        checkRunnersScored();
        count.reset();
    }
    
    private void advanceRunnersWalk()
    {
        int i = 0;
        while (i < runners.size() && runners.get(i) == i+1)
        {
            runners.set(i, i+2);
            i++;
        }
        runners.addFirst(1);
        pitchingTeam.walkBatter();
        checkRunnersScored();
        count.reset();
    }
    
    private void checkRunnersScored()
    {
        int runsScored = 0;
        while (!runners.isEmpty() && runners.getLast() >= 4)
        {
            runsScored++;
            battingTeam.scoreRun();
            runners.removeLast();
        }
        if (runsScored > 0)
        {
            message += (", " + runsScored + (runsScored != 1 ? " runs" : " run") + " scored!");
        }
        checkGameOver();
    }
    
    public void flyOut()
    {
        message = "Flyout";
        outMade();
    }
    
    public void groundOut()
    {
        message = "Groundout";
        outMade();
    }
    
    private void outMade()
    {
        outs++;
        count.reset();
        checkGameOver();
        if (outs >=3 && !isGameOver)
        {
            nextHalfInning();
        }
    }
    
    private void nextHalfInning()
    {
        outs = 0;
        if (isTopOfInning)
        {
            isTopOfInning = false;
            battingTeam = homeTeam;
            pitchingTeam = awayTeam;
            message = "Switch sides";
        }
        else
        {
            isTopOfInning = true;
            inning++;
            battingTeam = awayTeam;
            pitchingTeam = homeTeam;
            message = (inning != NUM_INNINGS + 1 ? "Next inning" : "Extra Innings!");
        }
        runners.clear();
        battingTeam.inningsRunsScored.add(0);
    }
    
    private void checkGameOver()
    {
        if (inning >= NUM_INNINGS)
        {
            if (isTopOfInning)
            {
                if (outs >= 3 && homeTeam.getRuns() > awayTeam.getRuns())
                {
                    isGameOver = true;
                    message = "Home Team has won!";
                }
            }
            else
            {
                if (homeTeam.getRuns() > awayTeam.getRuns())
                {
                    isGameOver = true;
                    message = "Walkoff " + message.split(",")[0] + ", Home Team has won!"; 
                }
                else
                {
                    if (outs >= 3 && homeTeam.getRuns() < awayTeam.getRuns())
                    {
                        isGameOver = true;
                        message = "Away Team has won!"; 
                    }
                }
            }
        } 
    }
    
    public void display()
    {
        char first = (runners.contains(1) ? 'X' : 'O');
        char second = (runners.contains(2) ? 'X' : 'O');
        char third = (runners.contains(3) ? 'X' : 'O');
        String inningText = (isTopOfInning ? "TOP " : "BOT ") + inning;
        System.out.println(String.format("%-" + namePadding + "s  R  H  W  |  %c  |", inningText, second));
        System.out.println(String.format("%-" +  namePadding + "s   %-2d %-2d %-2d |%c   %c|", awayTeam.name, awayTeam.getRuns(), awayTeam.getHits(), awayTeam.getWalks(), third, first));
        System.out.println(String.format("%-" +  namePadding + "s   %-2d %-2d %-2d |  O  |", homeTeam.name, homeTeam.getRuns(), homeTeam.getHits(), homeTeam.getWalks(), third, first));
        System.out.println(count.getDisplayString() + ", " + outs + (outs != 1 ? " outs" : " out"));
        System.out.println(message);
    } 

    public void displayFinal()
    {
        String headingText = (inning <= NUM_INNINGS ? "FINAL" : "FIN/" + inning);
        headingText = String.format("%-" + namePadding + "s  | ", headingText);
        for (int i = 1; i <= awayTeam.inningsRunsScored.size(); i++)
        {
            headingText += (String.format("%-2d ",i));
        }
        headingText += "|R  H  W";
        
        String spacingString = "";
        for (int i = 0; i < headingText.length(); i++) 
        {
            spacingString += '-';
        }
        
        String awayString = String.format("%-" + namePadding + "s   | ", awayTeam.name);
        String homeString = String.format("%-" + namePadding + "s   | ", homeTeam.name);
        for (int i=0; i < awayTeam.inningsRunsScored.size(); i++)
        {
            awayString += String.format("%-2d ",awayTeam.inningsRunsScored.get(i));
            if (i < homeTeam.inningsRunsScored.size())
            {
                homeString += String.format("%-2d ",homeTeam.inningsRunsScored.get(i));
            }
            else
            {
                homeString += "-  ";
            }
        }
        awayString += String.format("|%-2d %-2d %-2d", awayTeam.getRuns(), awayTeam.getHits(), awayTeam.getWalks());
        homeString += String.format("|%-2d %-2d %-2d", homeTeam.getRuns(), homeTeam.getHits(), homeTeam.getWalks());
        
        System.out.println(headingText);
        System.out.println(spacingString);
        System.out.println(awayString);
        System.out.println(homeString);
        System.out.println(spacingString);
        System.out.println("");
    }
}