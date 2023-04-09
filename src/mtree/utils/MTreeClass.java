package mtree.utils;

import DataStructure.Vector;
import mtree.*;
import java.util.Set;

public class MTreeClass extends MTree<Vector> {

    private static final PromotionFunction<Vector> nonRandomPromotion = new PromotionFunction<Vector>() {
        @Override
        public Pair<Vector> process(Set<Vector> vectorSet, DistanceFunction<? super Vector> distanceFunction) {
            return Utils.minMax(vectorSet);
        }
    };


    public MTreeClass() {
        super(2, DistanceFunctions.EUCLIDEAN,
                new ComposedSplitFunction<Vector>(
                        nonRandomPromotion,
                        new PartitionFunctions.BalancedPartition<Vector>()
                )
        );
    }

    public void add(Vector vector) {
        super.add(vector);
        _check();
    }

    public boolean remove(Vector vector) {
        boolean result = super.remove(vector);
        _check();
        return result;
    }

    public DistanceFunction<? super Vector> getDistanceFunction() {
        return distanceFunction;
    }
}
