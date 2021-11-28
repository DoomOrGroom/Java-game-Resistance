package cits3001_2021;

import java.util.*;

public class MyAgent implements Agent{

    private boolean isSpy;      // I am a spy
    private int id;
    private Double[] SpyProbability;        // The probability of the spy
    private String name;
    private int playersCnt;
    private int spiesCnt;
    private HashSet<Integer> SpiesSet;      // Store a player index that must be a spy
    private static final int[] spyNum = {2, 2, 3, 3, 3, 4}; // spyNum[n-5] is the number of spies in an n player game

    private static int AgentNum = 0;

    public MyAgent(){
        this.name = MyAgent.GetUniqueName();
    }

    /**
     * Gets a unique name
     * @return the unique name
     */
    private static String GetUniqueName(){
        ++MyAgent.AgentNum;
        switch(MyAgent.AgentNum){
            case 1: return "MyAgent-the-1st";
            case 2: return "MyAgent-the-2nd";
            case 3: return "MyAgent-the-3rd";
            default: return "MyAgent-the-" + MyAgent.AgentNum + "th";
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void newGame(int numPlayers, int playerIndex, int[] spies) {
        this.id = playerIndex;
        this.combineList = new ArrayList<>();
        this.stack = new Stack<>();
        this.isSpy = false;
        this.SpiesSet = new HashSet<>();
        this.playersCnt = numPlayers;
        this.spiesCnt = spyNum[numPlayers - 5];
        this.SpyProbability = new Double[numPlayers];
        for(int i = 0;i < spies.length;++i){
            this.SpiesSet.add(spies[i]);
            if(this.id == spies[i]){
                this.isSpy = true;
            }
        }
        for(int i = 0;i < numPlayers;++i){
            this.SpyProbability[i] = ((this.spiesCnt * 1.0) / (numPlayers - 1));
        }
    }

    /**
     * Determines whether an element is in an array
     * @param member
     * @param memberList
     * @return
     */
    private boolean MemberInList(int member, int[]memberList){
        for(int i = 0;i < memberList.length;++i){
            if(memberList[i] == member){
                return true;
            }
        }
        return false;
    }

    /**
     * Select the smallest index of n elements
     * @param member
     * @param n
     * @return
     */
    private Set<Integer> SelectMinElement(Double []member, int n){
        Set<Integer> ans = new HashSet<>();
        for(int i = 0;i < n;++i){
            double minValue = member[0];
            int minIndex = 0;
            for(int j = 1;j < member.length;++j){
                if(member[j] < minValue){
                    minIndex = j;
                    minValue = member[j];
                }
            }
            ans.add(minIndex);member[minIndex] = 2.0;
        }
        return ans;
    }

    /**
     * Select the index of the largest n elements
     * @param member
     * @param n
     * @return
     */
    private Set<Integer> SelectMaxElement(Double []member, int n){
        Set<Integer> ans = new HashSet<>();
        for(int i = 0;i < n;++i){
            double minValue = member[0];
            int minIndex = 0;
            for(int j = 1;j < member.length;++j){
                if(member[j] > minValue){
                    minIndex = j;
                    minValue = member[j];
                }
            }
            ans.add(minIndex);member[minIndex] = -1.0;
        }
        return ans;
    }

    @Override
    public int[] proposeMission(int teamsize, int failsRequired) {
        int []missionList = new int[teamsize];
        if(this.isSpy){
            int SpyCnt = failsRequired;
            int cnt = 0;
            while (SpyCnt >= this.SpiesSet.size()){
                --SpyCnt;
            }
            Double []SpyPro = new Double[this.playersCnt];
            for(int i = 0;i < this.playersCnt;++i){
                if(this.SpiesSet.contains(i)){
                    SpyPro[i] = this.SpyProbability[i];
                }else{
                    SpyPro[i] = 2.0;
                }
            }
            Set<Integer> minSpyPro = this.SelectMinElement(SpyPro, SpyCnt);
            for(Integer i : minSpyPro){
                missionList[cnt++] = i;
            }
            Double []ResistancePro = new Double[this.playersCnt];
            for(int i = 0;i < this.playersCnt;++i){
                if(!this.SpiesSet.contains(i)){
                    ResistancePro[i] = this.SpyProbability[i];
                }else{
                    ResistancePro[i] = -1.0;
                }
            }
            Set<Integer> maxResistancePro = this.SelectMaxElement(ResistancePro, teamsize - SpyCnt);
            for(Integer i : maxResistancePro){
                missionList[cnt++] = i;
            }
        }else{
            int cnt = 0;
            Double[] pro = this.SpyProbability.clone();
            for(Integer i : this.SpiesSet){
                pro[i] = 2.0;
            }
            Set<Integer> set = this.SelectMinElement(pro, teamsize);
            for(Integer i : set){
                missionList[cnt++] = i;
            }
        }
        return missionList;
    }

    @Override
    public boolean vote(int[] mission, int leader) {
        if(this.isSpy){
            if(this.SpiesSet.contains(leader)){
                return true;
            }else{
                boolean haveSpy = false;
                for(int i = 0;i < mission.length;++i){
                    if(this.SpiesSet.contains(mission[i])){
                        haveSpy = true;
                        break;
                    }
                }
                return haveSpy;
            }
        }else{
            int failPlayerCnt = 0;
            if(mission.length % 2 == 1) failPlayerCnt = mission.length / 2;
            else failPlayerCnt = mission.length / 2 - 1;
            int isSpyCnt = 0;
            int proSpyCnt = 0;
            for(int i = 0;i < mission.length;++i){
                if(this.SpiesSet.contains(i)) ++isSpyCnt;
                else if(this.SpyProbability[i] >= 0.6){
                    ++proSpyCnt;
                }
            }
            if((isSpyCnt + proSpyCnt) > failPlayerCnt) return false;
        }
        return true;
    }

    /**
     * Get the combination of pairCnt elements in an array
     * @param IndexList  The array to be composed
     * @param pairCnt How many combinations of elements
     * @param has How many elements are there currently
     * @param cur The current selected index
     * */
    private void combineElement(int []IndexList, int pairCnt, int has, int cur){
        if(has == pairCnt) {
            HashSet<Integer> set = new HashSet<>();
            for(Integer i : stack){
                set.add(i);
            }
            this.combineList.add(set);
            return;
        }
        for(int i = cur;i < IndexList.length;i++) {
            if(!stack.contains(IndexList[i])) {
                stack.add(IndexList[i]);
                combineElement(IndexList, pairCnt, has+1, i);
                stack.pop();
            }
        }
    }

    private Stack<Integer> stack;
    private List<Set<Integer>> combineList;

    /**
     * Perform Bayesian analysis from a given task queue and number of betrayer
     * @param mission The mission queue
     * @param failCnt The betrayer count
     * @return
     */
    private double[] BayesianAnalysis(int []mission, int failCnt){
        this.stack.clear();
        this.combineList.clear();
        double []rate = new double[this.playersCnt];        // P(T|A)
        int []playerList = new int[this.playersCnt - 1];
        int cnt = 0;
        for(int i = 0;i < this.playersCnt;++i){
            if(i == this.id) continue;
            playerList[cnt++] = i;
        }
        List<Set<Integer>> allList = new ArrayList<>();      // ab ac ad bc bd cd
        this.combineElement(playerList, this.spiesCnt, 0, 0);
        for(Set s : this.combineList){
            allList.add(s);
        }
        this.stack.clear();
        this.combineList.clear();
        int proCnt = 0;
        for(int i = 0;i < allList.size();++i){
            Set<Integer> nowSet = allList.get(i);
            int inCnt = 0;
            for(Integer id : nowSet){
                if(this.MemberInList(id, mission)){
                    ++inCnt;
                }
            }
            if(inCnt == failCnt){
                ++proCnt;
            }
        }
        // Cal P(T)
        double PT = (double) proCnt / allList.size();
        if(PT == 0){
            return new double[0];
        }
        // Cal P(T|A)
        for(int i = 0;i < this.playersCnt ;++i){
            if(i == this.id) continue;
            List<Set<Integer>> nowProList = new ArrayList<>();
            for(Set s : allList){
                if(s.contains(i)) nowProList.add(s);
            }
            int pro = 0;
            for(Set<Integer> s : nowProList){
                int inCnt = 0;
                for(Integer id : s){
                    if(this.MemberInList(id, mission)){
                        ++inCnt;
                    }
                }
                if(inCnt == failCnt){
                    ++pro;
                }
            }
            double nowPlayerPro = (double) pro / nowProList.size();
            rate[i] = nowPlayerPro;
        }
        double []ans = new double[this.playersCnt];
        for(int i = 0;i < this.playersCnt;++i){
            ans[i] = this.SpyProbability[i] * rate[i] / PT;
        }
        return ans;
    }

    @Override
    public void voteOutcome(int[] mission, int leader, boolean[] votes) {
        // do nothing
    }

    @Override
    public boolean betray(int[] mission, int leader) {
        boolean allSpy = true;
        for(Integer i : this.SpiesSet){
            if(!this.MemberInList(i, mission)){
                allSpy = false;
            }
        }
        if(allSpy) return false;
        int SpyCnt = 0;
        for(int i = 0;i < mission.length;++i){
            if(this.SpiesSet.contains(i)) ++SpyCnt;
        }
        int failCnt = 0;
        if(SpyCnt % 2 == 0) failCnt = SpyCnt / 2;
        else failCnt = SpyCnt / 2 + 1;
        int highProCnt = 0;
        for(int i = 0;i < mission.length;++i){
            if(this.SpiesSet.contains(i)) {
                if(this.SpyProbability[i] > 0.6) ++highProCnt;
            }
        }
        if(highProCnt >= failCnt) return false;
        return true;
    }

    @Override
    public void missionOutcome(int[] mission, int leader, int numFails, boolean missionSuccess) {
        if(mission.length == numFails){
            for(int i = 0;i < mission.length;++i){
                this.SpiesSet.add(i);
            }
            return ;
        }
        double []rate = this.BayesianAnalysis(mission, numFails);
        // The spy hid himself, and the result was inconsequential
        if(rate.length == 0) return ;
        for(int i = 0;i < this.SpyProbability.length;++i){
            this.SpyProbability[i] = (this.SpyProbability[i] + rate[i]) / 2;
            System.out.println(i + "th player`s bayes" + rate[i]);
            System.out.println(i + "th player`s probability" + this.SpyProbability[i]);
        }
    }

    @Override
    public void roundOutcome(int roundsComplete, int roundsLost) {
        // do nothing
    }

    @Override
    public void gameOutcome(int roundsLost, int[] spies) {
        // do nothing
    }

}
