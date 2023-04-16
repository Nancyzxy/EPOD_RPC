package Handler;

import Framework.EdgeNodeImpl;

import java.util.List;

import utils.Constants;

public class MCODHandler extends Handler {
    public MCODHandler(EdgeNodeImpl node) {
        super(node);
    }

    @Override
    public boolean neighboringSet(List<Double> c1, List<Double> c2) {
    // 1.暴力计算
        double sum = 0;
        for (int i = 0; i < c1.size(); i++) {
            sum += (Math.pow((double) c1.get(i) - (double) c2.get(i), 2));
        }
        return sum <= 4 * Constants.R * Constants.R;
    // 2.看使用环境，如果是map_to_MCO有的，可以映射回原点用distance.calculate
    // 3.new了两个点 是2R之内
    // MCO a = new MCO(new Vector(c1.stream().collect(Collectors.toList())));
    }
}
