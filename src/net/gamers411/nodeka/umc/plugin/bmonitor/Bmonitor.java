package net.gamers411.nodeka.umc.plugin.bmonitor;

import com.lsd.umc.script.ScriptInterface;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bmonitor
{
  private String version = "1.0.0";
  private ScriptInterface script;
  private Pattern pdamage;
  private Pattern phits;
  private int totalDamage;
  private int hitsAttempted;
  private int hitsLanded;
  private int rounds;
  private int minAttacks;
  private int maxDamage;
  private int ignored;

  public void init(ScriptInterface script)
  {
    this.script = script;
    this.pdamage = Pattern.compile("(tickl|graz|scratch|bruis|sting|wound|shend|scath|pummel|batter|splinter|disfigur|fractur|lacerat|ruptur|mutilat|dehisc|maim|dismem|sunder|cremat|eviscerat|ravag|immolat|liquif|vaporiz|atomiz|obliterat|etherealiz|eradicat)\\w+ \\((\\d+)\\)", 2);

    this.phits = Pattern.compile("\\[ (\\d+) of (\\d+) \\]");

    reset();

    this.minAttacks = 0;
    this.maxDamage = 0;

    script.registerCommand("BMONITOR", "net.gamers411.nodeka.umc.plugin.bmonitor.Bmonitor", "commandLine");

    script.print("Battle Monitor v" + this.version + "\001");
    script.print(" - by Nemesis - http://nodeka.gamers411./");
  }

  public void IncomingEvent(ScriptInterface script)
  {
    String line = script.getText();
    Matcher dmg;
    Matcher hits;
    if ((line.startsWith("You land")) && ((dmg = this.pdamage.matcher(line)).find()) && ((hits = this.phits.matcher(line)).find()))
    {
      if (((Integer.parseInt(hits.group(2)) < this.minAttacks) && (this.minAttacks != 0)) || ((Integer.parseInt(dmg.group(2)) <= this.maxDamage) || (this.maxDamage == 0)))
      {
        this.totalDamage += Integer.parseInt(dmg.group(2));
        this.hitsLanded += Integer.parseInt(hits.group(1));
        this.hitsAttempted += Integer.parseInt(hits.group(2));
        this.rounds += 1;
      }
      else {
        this.ignored += 1;
      }
    }
  }

  public String commandLine(String args) {
    String[] argArray = args.toLowerCase().split(" ");

    if (args.length() == 0) {
      this.script.print("#bmonitor Available Commands -\001");
      this.script.print(" - set\001");
      this.script.print(" - set maxdmg <number>\001");
      this.script.print(" - set minattacks <number>\001");
      this.script.print(" - disp or show\001");
      this.script.print(" - reset or clear\001");
      this.script.print("Syntax:  #bmonitor <command> <argument>\001");
      this.script.print("Example: #bmonitor show");
      this.script.print("         #bmonitor reset");
      return "";
    }

    if (argArray[0].equals("set"))
    {
      if (argArray.length == 1)
      {
        this.script.capture("[bMonitor]: maxdmg = " + this.maxDamage + ", minAttackss = " + this.minAttacks + "\001");
        return "";
      }
      if (argArray[1].startsWith("maxd"))
      {
        if (argArray.length == 3)
        {
          this.maxDamage = Integer.parseInt(argArray[2]);
          this.script.capture("[bMonitor]: maxdmg = " + this.maxDamage + "\001");
        }
        else {
          this.script.capture("[bMonitor]: maxdmg = " + this.maxDamage + "\001");
        }
      } else if (argArray[1].startsWith("mina"))
      {
        if (argArray.length == 3)
        {
          this.minAttacks = Integer.parseInt(argArray[2]);
          this.script.capture("[bMonitor]: minAttacks = " + this.minAttacks + "\001");
        }
        else {
          this.script.capture("[bMonitor]: minAttacks = " + this.minAttacks + "\001");
        }
      }
    } else if ((argArray[0].startsWith("res")) || (argArray[0].startsWith("cle")))
    {
      reset();
      displayStats();
    }
    else if ((argArray[0].startsWith("disp")) || (argArray[0].startsWith("sho")))
    {
      displayStats();
    } else {
      this.script.print("[bMonitor]: Unknown command.");
    }
    return "";
  }

  private void reset()
  {
    this.rounds = 0;
    this.hitsAttempted = 0;
    this.hitsLanded = 0;
    this.ignored = 0;
    this.totalDamage = 0;
  }

  private void displayStats()
  {
    int accuracy = Math.round(this.hitsLanded / this.hitsAttempted * 100.0F);
    int avgHitLanded = Math.round(this.hitsLanded / this.rounds);
    int avgHitAttempted = Math.round(this.hitsAttempted / this.rounds);
    int avgHitDmg = Math.round(this.totalDamage / this.rounds);

    this.script.capture("[bMonitor]: Rounds - " + this.rounds + " - Ignored - " + this.ignored + " - Hits - " + this.hitsLanded + " of " + this.hitsAttempted + " [" + accuracy + "%] - Dmg - " + this.totalDamage + "\001");

    this.script.capture("            Hits Per Round - " + avgHitLanded + " of " + avgHitAttempted + " - Dmg per hit - " + avgHitDmg + "\001");
  }
}