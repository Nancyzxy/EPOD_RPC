package Handler;

import Framework.EdgeNodeImpl;
import utils.Constants;
import java.util.List;

public class NETSHandler extends Handler{

    public NETSHandler(EdgeNodeImpl node) {
        super(node);
    }

    @Override
    public boolean neighboringSet(List<Double> c1, List<Double> c2) {
        double ss = 0;
        double neighCellIdxDist = Math.sqrt(Constants.subDim)*2;
        double neighCellFullDimIdxDist = Math.sqrt(Constants.dim)*2;
        double cellIdxDist = (c1.size() == Constants.dim? neighCellFullDimIdxDist : neighCellIdxDist);
        double threshold =cellIdxDist*cellIdxDist;
        for(int k = 0; k<c1.size(); k++) {
            short x1 = (short) (double)c1.get(k);
            short x2 = (short) (double)c2.get(k);
            ss += Math.pow(x1-x2,2);
            if (ss >= threshold) return false;
        }
        return true;
    }
}
