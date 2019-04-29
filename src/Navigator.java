import java.util.ArrayList;
import java.util.ListIterator;

public class Navigator<T> {
    private ArrayList<T> strings;
    private final StringBuilder builder;
    private int pageSize;
    private int nCols;
    private int nPages;
    private int page;

    public Navigator(ArrayList<T> strings, int pageSize, int nCols) {
        this.builder = new StringBuilder();
        this.strings = strings;
        this.pageSize = pageSize;
        this.nCols = nCols;
        this.nPages = (strings.size()/(pageSize*nCols))
                - ((strings.size() % (pageSize * nCols) != 0) ? 0 : 1);
        this.page = 0;

    }

    public void next() {
        this.page = (page + 1 <= nPages)?page + 1 : page;
    }

    public void previous() {
        this.page = (page - 1 >= 0)?page -1 : page;
    }

    @Override
    public String toString(){
        StringBetter spac = new StringBetter(" ");
        StringBetter senter = new StringBetter("\n");
        int pos, r = 0;
        builder.setLength(0);
        for(int i = this.pageSize * this.page; i < this.pageSize * (this.page + 1); i++){
            for(int j = 0; j < this.nCols && j + i * this.nCols < this.strings.size(); j++){
                pos = j + i * this.nCols;
                builder.append(pos + 1).append(".");
                spac.repeate(this.strings.get(pos).toString().length() + 1 - String.valueOf(pos + 1).length());
                builder.append(this.strings.get(pos)).append("      ");
            }
            r++;
            builder.append("\n");
        }
        builder.append(senter.repeate(this.pageSize - r  + 2));
        return builder.toString();
    }
}
