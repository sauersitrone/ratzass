package de.simone.command;

import java.util.LinkedList;

import com.hstairs.ppmajal.PDDLProblem.PDDLPlanner;
import com.hstairs.ppmajal.PDDLProblem.PDDLProblem;
import com.hstairs.ppmajal.PDDLProblem.PDDLSolution;
import com.hstairs.ppmajal.PDDLProblem.PDDLState;
import com.hstairs.ppmajal.domain.PDDLDomain;
import com.hstairs.ppmajal.extraUtils.Utils;
import com.hstairs.ppmajal.pddl.heuristics.PDDLHeuristic;
import com.hstairs.ppmajal.search.SearchHeuristic;
import com.hstairs.ppmajal.transition.TransitionGround;
import com.hstairs.ppmajal.transition.Transition.Semantics;

import lombok.extern.java.Log;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Log
public class RENHSP {
   private String domainFile;
   private String problemFile;
   private String searchEngineString;
   private String wh;
   private String heuristic = "aibr";
   private String gw;
   private boolean savingSearchSpaceJson = false;
   private String deltaExecution;
   private float depthLimit;
   private String savePlan;
   private int linearEffectsAbstraction = -1;
   private boolean printTrace;
   private String tieBreaking;
   private String planner;
   private String deltaHeuristic;
   private String deltaPlanning;
   private String deltaValidation;
   private boolean helpfulActions;
   private Integer numSubdomains;
   private PDDLProblem problem;
   private boolean pddlPlus;
   private PDDLDomain domain;
   private PDDLDomain domainHeuristic;
   private PDDLProblem heuristicProblem;
   private long overallStart;
   private boolean copyOfTheProblem;
   private boolean anyTime;
   private long timeOut;
   private boolean aibrPreprocessing;
   private SearchHeuristic h;
   private long overallPlanningTime;
   private float endGValue;
   private boolean helpfulTransitions;
   private boolean internalValidation = false;
   private int planLength;
   private String redundantConstraints;
   private String groundingType;
   private boolean naiveGrounding;
   private boolean stopAfterGrounding;
   private boolean printEvents;
   private boolean sdac;
   private boolean onlyPlan;
   private boolean ignoreMetric;
   private boolean printActions;
   private String inputPlan;
   private PrintStream out;
   private boolean autoAnytime;
   private boolean unitCostHeuristic;
   private boolean printAllInfo;
   private boolean printMakespan;
   public static boolean aibrDebug = false;
   private boolean pls;
   LinkedList<AnytimeConfigurations> conf = new LinkedList();

   public RENHSP(boolean var1) {
      this.copyOfTheProblem = var1;
   }

   public int getPlanLength() {
      return this.planLength;
   }

   public Pair<PDDLDomain, PDDLProblem> parseDomainProblem(String var1, String var2, String var3, PrintStream var4) {
      try {
         PDDLDomain var5 = new PDDLDomain(var1);
         this.pddlPlus = !var5.getProcessesSchema().isEmpty() || !var5.getEventsSchema().isEmpty();
         var4.println("Domain parsed");
         PDDLProblem var6 = new PDDLProblem(var2, var5.getConstants(), var5.getTypes(), var5, var4, this.groundingType, this.sdac, this.ignoreMetric, new BigDecimal(this.deltaPlanning), new BigDecimal(this.deltaExecution));
         if (!var5.getProcessesSchema().isEmpty()) {
            var6.setDeltaTimeVariable(var3);
         }

         var4.println("Problem parsed");
         var4.println("Grounding..");
         if (!var6.prepareForSearch(this.aibrPreprocessing, this.stopAfterGrounding)) {
            return null;
         } else {
            if (this.printActions) {
               System.out.println(var6.getTransitions());
            }

            if (this.printAllInfo) {
               var6.printAllInfo();
            }

            if (this.stopAfterGrounding) {
               System.exit(1);
            }

            return Pair.of(var5, var6);
         }
      } catch (Exception var7) {
         Logger.getLogger(RENHSP.class.getName()).log(Level.SEVERE, (String)null, var7);
         return null;
      }
   }

   public boolean parsingDomainAndProblem(String[] var1) {
      try {
         this.overallStart = System.currentTimeMillis();
         Pair var2 = this.parseDomainProblem(this.domainFile, this.problemFile, this.deltaExecution, System.out);
         if (var2 == null) {
            return false;
         }

         this.domain = (PDDLDomain)var2.getKey();
         this.problem = (PDDLProblem)var2.getRight();
         if (this.pddlPlus) {
            System.out.println("Heuristic Problem Creation");
            var2 = this.parseDomainProblem(this.domainFile, this.problemFile, this.deltaHeuristic, System.out);
            this.domainHeuristic = (PDDLDomain)var2.getKey();
            this.heuristicProblem = (PDDLProblem)var2.getRight();
            this.copyOfTheProblem = true;
         } else {
            this.heuristicProblem = this.problem;
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return true;
   }

   public void configurePlanner() {
      if (this.planner != null) {
         this.setPlanner();
      }

   }

   public LinkedList<ImmutablePair<BigDecimal, TransitionGround>> planning() {
      try {
         this.printStats();
         this.setHeuristic();
         if (this.autoAnytime) {
            this.conf.add(new AnytimeConfigurations("lazygbfs", "hmrp", true, "4"));
            this.conf.add(new AnytimeConfigurations("lazywastar", "hmrp", false, "8"));
            this.conf.add(new AnytimeConfigurations("lazywastar", "hmrp", false, "4"));
            this.conf.add(new AnytimeConfigurations("lazywastar", "hmrp", false, "2"));
            this.conf.add(new AnytimeConfigurations("lazywastar", "hmrp", false, "1"));
            this.conf.add(new AnytimeConfigurations("wastar", "hmrp", false, "1"));
         }

         int var1 = 0;

         do {
            if (this.autoAnytime && this.conf.size() > var1) {
               AnytimeConfigurations var2 = (AnytimeConfigurations)this.conf.get(var1);
               this.searchEngineString = var2.search;
               this.heuristic = var2.heuristic;
               this.helpfulActions = var2.ha;
               this.wh = var2.wh;
            }

            LinkedList var5 = this.search();
            if (this.printTrace) {
               String var10000 = this.getProblem().getPddlFileReference();
               String var3 = var10000 + "_search_" + this.searchEngineString + "_h_" + this.heuristic + "_break_ties_" + this.tieBreaking + ".npt";
               this.problem.validate(var5, new BigDecimal(this.deltaExecution), new BigDecimal(this.deltaExecution), var3);
               System.out.println("Numeric Plan Trace saved to " + var3);
            }

            if(var5 != null) {
               return var5;
            }
            // if (var5 == null) {
            //    return null;
            // }

            this.depthLimit = this.endGValue;
            if (this.anyTime) {
               System.out.println("NEW COST ==================================================================================>" + this.depthLimit);
            }

            Object var6 = null;
            System.gc();
            ++var1;
         } while(this.anyTime);
      } catch (Exception var4) {
         Logger.getLogger(RENHSP.class.getName()).log(Level.SEVERE, (String)null, var4);
      }

      return null;
   }

   public void parseInput(String[] var1) {
      Options var2 = new Options();
      var2.addRequiredOption("o", "domain", true, "PDDL domain file");
      var2.addRequiredOption("f", "problem", true, "PDDL problem file");
      var2.addOption("planner", true, "Fast Preconfgured Planner. For available options look into the code. This overrides all other parameters but domain and problem specs. Commonly used settings are: sat-hmrp (satisficing planning) or opt-hrmax (optimal planning).");
      var2.addOption("h", true, "heuristic: options (default is hadd):\naibr, Additive Interval Based relaxation heuristic\nhadd, Additive version of subgoaling heuristic\nhradd, Additive version of subgoaling heuristic plus redundant constraints\nhmax, Hmax for Numeric Planning\nhrmax, Hmax for Numeric Planning with redundant constraints\nhmrp, heuristic based on MRP extraction\nblcost, goal sensitive heuristic (1 to non goal-states, 0 to goal-states)\nblind, full blind heuristic (0 to all states)ngc, Numeric Goal Counting Heuristic");
      var2.addOption("s", true, "allows to select search strategy (default is WAStar):\ngbfs, Greedy Best First Search (f(n) = h(n))\nlazygbfs, Greedy Best First Search (f(n) = h(n)) with lazy evaluation\nWAStar, WA* (f(n) = g(n) + h_w*h(n))\nwa_star_4, WA* (f(n) = g(n) + 4*h(n))\n");
      var2.addOption("ties", true, "tie-breaking (default is arbitrary): larger_g, smaller_g, arbitrary");
      var2.addOption("dp", "delta_planning", true, "planning decision executionDelta: float");
      var2.addOption("de", "delta_execution", true, "planning execution executionDelta: float");
      var2.addOption("dh", "delta_heuristic", true, "planning heuristic executionDelta: float");
      var2.addOption("dv", "delta_validation", true, "validation executionDelta: float");
      var2.addOption("d", "delta", true, "Override other delta_<planning,execuction,validation,heuristic> configurations: float");
      var2.addOption("epsilon", true, "epsilon separation: float");
      var2.addOption("wh", true, "h-values weight: float");
      var2.addOption("sjr", false, "save state space explored in json file");
      var2.addOption("ha", "helpful-actions", true, "activate helpful actions in the search");
      var2.addOption("pe", "print-events-plan", false, "activate printing of events");
      var2.addOption("ht", "helpful-transitions", true, "activate up-to-macro actions");
      var2.addOption("sp", true, "Save plan. Argument is filename");
      var2.addOption("pt", false, "print state trajectory (Experimental)");
      var2.addOption("im", false, "Ignore Metric in the heuristic");
      var2.addOption("dap", false, "Disable Aibr Preprocessing");
      var2.addOption("red", "redundant_constraints", true, "Choose mechanism for redundant constraints generation among, no, brute and smart. No redundant constraints generation is the default");
      var2.addOption("gro", "grounding", true, "Activate grounding via internal mechanism, fd or metricff or internal or naive (default is internal)");
      var2.addOption("dl", true, "bound on plan-cost: float (Experimental)");
      var2.addOption("k", true, "maximal number of subdomains. This works in combination with haddabs: integer");
      var2.addOption("anytime", false, "Run in anytime modality. Incrementally tries to find a lower bound. Does not stop until the user decides so");
      var2.addOption("timeout", true, "Timeout for anytime modality");
      var2.addOption("stopgro", false, "Stop After Grounding");
      var2.addOption("ival", false, "Internal Validation");
      var2.addOption("sdac", false, "Activate State Dependent Action Cost (Very Experimental!)");
      var2.addOption("onlyplan", false, "Print only the plan without waiting");
      var2.addOption("print_actions", false, "Print all actions after grounding");
      var2.addOption("tolerance", true, "Numeric tolerance in evaluating numeric conditions. Default is 0.00001");
      var2.addOption("inputplan", true, "Insert the name of the file containing the plan to validate. This is to be used with ival activated");
      var2.addOption("silent", false, "Activate silent modality");
      var2.addOption("autoanytime", false, "Activate auto anytime modality. ");
      var2.addOption("uch", false, "Pretend all actions cost one in the heuristic");
      var2.addOption("npm", false, "PDDL+ feature: Do not print makespan in the plan");
      var2.addOption("pai", false, "Print all info before search");
      var2.addOption("ea", true, "Effect abstraction mode for non-constants effects. Takes integer as an argument, denoting the number of intervals to consider");
      var2.addOption("aibr_debug", false, "Enable AIBR debug logging");
      var2.addOption("pls", false, "Print the very last state");
      DefaultParser var3 = new DefaultParser();

      try {
         CommandLine var4 = var3.parse(var2, var1);
         this.domainFile = var4.getOptionValue("o");
         this.problemFile = var4.getOptionValue("f");
         this.planner = var4.getOptionValue("planner");
         this.heuristic = var4.getOptionValue("h");
         String var12 = var4.getOptionValue("tolerance");
         if (var12 != null) {
            System.out.println(var12);
            Utils.tolerance = Double.parseDouble(var12);
         }

         if (this.heuristic == null) {
            this.heuristic = "hadd";
         }

         this.searchEngineString = var4.getOptionValue("s");
         if (this.searchEngineString == null) {
            this.searchEngineString = "gbfs";
         }

         this.tieBreaking = var4.getOptionValue("ties");
         this.deltaPlanning = var4.getOptionValue("dp");
         if (this.deltaPlanning == null) {
            this.deltaPlanning = "1.0";
         }

         var12 = var4.getOptionValue("red");
         if (var12 == null) {
            this.redundantConstraints = "no";
         } else {
            this.redundantConstraints = var12;
         }

         var12 = var4.getOptionValue("gro");
         if (var12 != null) {
            this.groundingType = var12;
         } else {
            this.groundingType = "internal";
         }

         this.pls = var4.hasOption("pls");
         String var6 = var4.getOptionValue("ea");
         if (var6 != null) {
            if (var6.equals("all")) {
               this.linearEffectsAbstraction = Integer.MAX_VALUE;
            } else {
               this.linearEffectsAbstraction = Integer.parseInt(var6);
            }
         }

         this.internalValidation = var4.hasOption("ival");
         this.unitCostHeuristic = var4.hasOption("uch");
         this.deltaExecution = var4.getOptionValue("de");
         if (this.deltaExecution == null) {
            this.deltaExecution = "1.0";
         }

         this.deltaHeuristic = var4.getOptionValue("dh");
         if (this.deltaHeuristic == null) {
            this.deltaHeuristic = "1.0";
         }

         this.deltaValidation = var4.getOptionValue("dv");
         if (this.deltaValidation == null) {
            this.deltaValidation = "1";
         }

         String var7 = var4.getOptionValue("dl");
         if (var7 != null) {
            this.depthLimit = Float.parseFloat(var7);
         } else {
            this.depthLimit = -1.0F;
         }

         String var8 = var4.getOptionValue("timeout");
         if (var8 != null) {
            this.timeOut = Long.parseLong(var8) * 1000L;
         } else {
            this.timeOut = Long.MAX_VALUE;
         }

         String var9 = var4.getOptionValue("delta");
         if (var9 != null) {
            this.deltaHeuristic = var9;
            this.deltaValidation = var9;
            this.deltaPlanning = var9;
            this.deltaExecution = var9;
         }

         this.inputPlan = var4.getOptionValue("inputplan");
         String var10 = var4.getOptionValue("k");
         if (var10 != null) {
            this.numSubdomains = Integer.parseInt(var10);
         } else {
            this.numSubdomains = 2;
         }

         this.gw = var4.getOptionValue("wg");
         this.wh = var4.getOptionValue("wh");
         this.savingSearchSpaceJson = var4.hasOption("sjr");
         if (var4.hasOption("silent")) {
            // this.out = new PrintStream(new 2(this));
         } else {
            this.out = System.out;
         }

         this.sdac = var4.hasOption("sdac");
         this.printMakespan = !var4.hasOption("npm");
         this.helpfulActions = var4.getOptionValue("ha") != null && "true".equals(var4.getOptionValue("ha"));
         this.autoAnytime = var4.hasOption("autoanytime");
         this.printEvents = var4.hasOption("pe");
         this.printTrace = var4.hasOption("pt");
         this.savePlan = var4.getOptionValue("sp");
         this.onlyPlan = var4.hasOption("onlyplan");
         this.anyTime = var4.hasOption("anytime");
         this.aibrPreprocessing = !var4.hasOption("dap");
         this.stopAfterGrounding = var4.hasOption("stopgro");
         this.helpfulTransitions = var4.getOptionValue("ht") != null && "true".equals(var4.getOptionValue("ht"));
         this.ignoreMetric = var4.hasOption("im");
         this.printActions = var4.hasOption("print_actions");
         this.printAllInfo = var4.hasOption("pai");
         aibrDebug = var4.hasOption("aibr-debug");
      } catch (ParseException var11) {
         System.err.println("Parsing failed.  Reason: " + var11.getMessage());
         HelpFormatter var5 = new HelpFormatter();
         var5.printHelp("enhsp", var2);
         System.exit(-1);
      }

   }

   public PDDLProblem getProblem() {
      return this.problem;
   }

   public void printStats() {
      System.out.println("|A|:" + this.getProblem().getActions().size());
      System.out.println("|P|:" + this.getProblem().getProcessesSet().size());
      System.out.println("|E|:" + this.getProblem().getEventsSet().size());
      if (this.pddlPlus) {
         System.out.println("Delta time heuristic model:" + this.deltaHeuristic);
         System.out.println("Delta time planning model:" + this.deltaPlanning);
         System.out.println("Delta time search-execution model:" + this.deltaExecution);
         System.out.println("Delta time validation model:" + this.deltaValidation);
      }

   }

   private void setPlanner() {
      this.helpfulTransitions = false;
      this.helpfulActions = false;
      this.tieBreaking = "arbitrary";
      switch (this.planner) {
         case "sat-hmrp":
            this.heuristic = "hmrp";
            this.searchEngineString = "gbfs";
            this.tieBreaking = "arbitrary";
            break;
         case "sat-hmrph":
            this.heuristic = "hmrp";
            this.helpfulActions = true;
            this.searchEngineString = "gbfs";
            this.tieBreaking = "arbitrary";
            break;
         case "sat-hmrphj":
            this.heuristic = "hmrp";
            this.helpfulActions = true;
            this.helpfulTransitions = true;
            this.searchEngineString = "gbfs";
            this.tieBreaking = "arbitrary";
            break;
         case "sat-hmrpff":
            this.heuristic = "hmrp";
            this.helpfulActions = false;
            this.redundantConstraints = "brute";
            this.helpfulTransitions = false;
            this.searchEngineString = "gbfs";
            this.tieBreaking = "arbitrary";
            break;
         case "sat-hadd":
            this.heuristic = "hadd";
            this.searchEngineString = "gbfs";
            this.tieBreaking = "smaller_g";
            break;
         case "sat-aibr":
            this.heuristic = "aibr";
            this.searchEngineString = "WAStar";
            this.tieBreaking = "arbitrary";
            break;
         case "sat-hradd":
            this.heuristic = "hradd";
            this.searchEngineString = "gbfs";
            this.tieBreaking = "smaller_g";
            break;
         case "opt-hmax":
            this.heuristic = "hmax";
            this.searchEngineString = "WAStar";
            this.tieBreaking = "larger_g";
            break;
         case "opt-hlm":
            this.heuristic = "hlm-lp";
            this.searchEngineString = "WAStar";
            this.tieBreaking = "larger_g";
            break;
         case "opt-hlmrd":
            this.heuristic = "hlm-lp";
            this.redundantConstraints = "brute";
            this.searchEngineString = "WAStar";
            this.tieBreaking = "larger_g";
            break;
         case "opt-hrmax":
            this.heuristic = "hrmax";
            this.searchEngineString = "WAStar";
            this.tieBreaking = "larger_g";
            break;
         case "opt-blind":
            this.heuristic = "blind";
            this.searchEngineString = "WAStar";
            this.tieBreaking = "larger_g";
            this.aibrPreprocessing = false;
            break;
         default:
            System.out.println("! ====== ! Warning: Unknown planner configuration. Going with default: gbfs with hadd ! ====== !");
            this.heuristic = "hadd";
            this.searchEngineString = "gbfs";
            this.tieBreaking = "smaller_g";
      }

   }

   private void setHeuristic() {
      this.h = PDDLHeuristic.getHeuristic(this.heuristic, this.heuristicProblem, this.redundantConstraints, this.helpfulActions, this.helpfulTransitions, this.unitCostHeuristic, this.linearEffectsAbstraction, aibrDebug);
   }

   private LinkedList<ImmutablePair<BigDecimal, TransitionGround>> search() throws Exception {
      PDDLPlanner var1 = new PDDLPlanner(this.searchEngineString, this.redundantConstraints, this.helpfulActions, this.helpfulTransitions, this.wh != null ? Float.parseFloat(this.wh) : 1.0F, this.deltaPlanning != null ? new BigDecimal(this.deltaPlanning) : new BigDecimal((double)1.0F), this.deltaExecution != null ? new BigDecimal(this.deltaExecution) : new BigDecimal((double)1.0F), this.tieBreaking == null ? "arbitrary" : this.tieBreaking, this.savingSearchSpaceJson, this.depthLimit == -1.0F ? Float.POSITIVE_INFINITY : this.depthLimit);
      if (this.savingSearchSpaceJson) {
        //  Runtime.getRuntime().addShutdownHook(new 3(this, var1));
      }

      this.overallStart = System.currentTimeMillis();
      PDDLSolution var2 = var1.plan(this.problem, this.h);
      this.overallPlanningTime = System.currentTimeMillis() - this.overallStart;
      this.endGValue = var2.gValueAtTheEnd();
      this.printInfo(var2, this.pddlPlus, this.savePlan, var2 == null ? null : var2.lastState());
      if (this.savingSearchSpaceJson) {
         var1.getSearchSpaceHandle().printJson(this.getProblem().getPddlFileReference() + ".sp_log");
      }

      return var2.rawPlan();
   }

   private void printInfo(PDDLSolution var1, boolean var2, String var3, PDDLState var4) {
      if (var1.rawPlan() != null) {
         System.out.println("Problem Solved\n");
         System.out.println("Found Plan:");
         this.printPlan(var1.rawPlan(), var2, var4, var3);
         System.out.println("\nPlan-Length:" + var1.rawPlan().size());
         this.planLength = var1.rawPlan().size();
      } else {
         System.out.println("Problem unsolvable");
      }

      if (var2 && var1.rawPlan() != null) {
         System.out.println("Elapsed Time: " + String.valueOf(var4.time));
      }

      System.out.println("Metric (Search):" + var1.gValueAtTheEnd());
      System.out.println("Planning Time (msec): " + this.overallPlanningTime);
      System.out.println("Heuristic Time (msec): " + var1.stats().heuristicTime());
      System.out.println("Search Time (msec): " + var1.stats().searchTime());
      System.out.println("Expanded Nodes:" + var1.stats().nodesExpanded());
      System.out.println("States Evaluated:" + var1.stats().nodesEvaluated());
      System.out.println("Number of Dead-Ends detected:" + var1.stats().deadEnds());
      System.out.println("Number of Duplicates detected:" + var1.stats().duplicates());
      if (this.pls) {
         System.out.println(var4);
      }

   }

   private void printPlan(LinkedList<ImmutablePair<BigDecimal, TransitionGround>> var1, boolean var2, PDDLState var3, String var4) {
      float var5 = 0.0F;
      ImmutablePair var6 = null;
      ArrayList var7 = new ArrayList();
      boolean var8 = false;
      int var9 = var1.size();
      int var10 = 0;

      for(ImmutablePair var12 : var1) {
         ++var10;
         if (!var2) {
            System.out.print(var5 + ": " + String.valueOf(var12.getRight()) + "\n");
            if (var4 != null) {
               TransitionGround var15 = (TransitionGround)var12.getRight();
               var7.add(var15.toString());
            }

            ++var5;
         } else {
            TransitionGround var13 = (TransitionGround)var12.getRight();
            if (var13.getSemantics() == Semantics.PROCESS) {
               if (!var8) {
                  var6 = var12;
                  var8 = true;
               }

               if (var10 == var9 && !this.onlyPlan) {
                  PrintStream var18 = System.out;
                  String var22 = String.valueOf(var6.getLeft());
                  var18.println(var22 + ": -----waiting---- [" + String.valueOf(var3.time) + "]");
               }
            } else if (var13.getSemantics() == Semantics.EVENT && !this.printEvents) {
               if (var10 == var9 && !this.onlyPlan) {
                  PrintStream var17 = System.out;
                  String var21 = String.valueOf(var6.getLeft());
                  var17.println(var21 + ": -----waiting---- [" + String.valueOf(var12.getLeft()) + "]");
               }
            } else {
               if (var8) {
                  var8 = false;
                  if (!this.onlyPlan) {
                     PrintStream var10000 = System.out;
                     String var10001 = String.valueOf(var6.getLeft());
                     var10000.println(var10001 + ": -----waiting---- [" + String.valueOf(var12.getLeft()) + "]");
                  }
               }

               PrintStream var16 = System.out;
               String var19 = String.valueOf(var12.getLeft());
               var16.print(var19 + ": " + String.valueOf(var12.getRight()) + "\n");
               if (var4 != null) {
                  var19 = String.valueOf(var12.getLeft());
                  var7.add(var19 + ": " + var13.toString());
               }
            }
         }
      }

      if (var4 != null) {
         try {
            if (var2 && this.printMakespan) {
               var7.add(String.valueOf(var3.time) + ": @PlanEND ");
            }

            Files.write(Path.of(var4), var7);
         } catch (IOException var14) {
            Logger.getLogger(RENHSP.class.getName()).log(Level.SEVERE, (String)null, var14);
         }
      }

   }

   public static record AnytimeConfigurations(String search, String heuristic, Boolean ha, String wh) {
   }
}

