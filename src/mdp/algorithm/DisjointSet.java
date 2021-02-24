package mdp.algorithm;

public class DisjointSet {
    private int[] par;
    private int[] rank;

    public DisjointSet(int n){
        par = new int[n];
        rank = new int[n];
        for(int i = 0; i < n; i++){
            par[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int x){
        return (par[x] == x) ? x : (par[x] = find(par[x]));
    }

    public void unite(int x, int y){
        x = find(x);
        y = find(y);
        if(x==y) return;

        if(rank[x] < rank[y]) par[x] = y;
        else{
            par[y] = x;
            if(rank[x] == rank[y]) rank[x]++;
        }
    }

    public boolean same(int x, int y){
        return find(x) == find(y);
    }

}
