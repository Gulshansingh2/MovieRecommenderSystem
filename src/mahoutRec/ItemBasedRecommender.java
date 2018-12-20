package mahoutRec;
import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.RandomUtils;
 
public class ItemBasedRecommender {
    public void item() throws Exception {
    	final Scanner scanner = new Scanner(System.in);
        RandomUtils.useTestSeed(); // to randomize the evaluation result         
        DataModel model = new FileDataModel(new File("dataMahout/dataset-recsys.csv"));
        System.out.print("Enter 1 for EuclideanDistance  , 2 for PearsonCorrelationSimilarity  , 3 for LogLikelihoodSimilarity and 4 for TanimotoCoefficient similarity : ");
     	final int simi = scanner.nextInt();
 
        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException { 
            	
             	ItemSimilarity similarity = null;
             	switch(simi) {
    	        case 1:
                similarity = new EuclideanDistanceSimilarity(model);
                break;
    	        case 2:
                similarity = new PearsonCorrelationSimilarity(model);   
                break;
    	        case 3:
    	         similarity = new LogLikelihoodSimilarity(model);
    	        break;
    	        case 4:
    	        similarity = new TanimotoCoefficientSimilarity(model); 
    	        break;
                default:
                	System.out.println("invalid");
             	}
                        
                //Optimizer optimizer = new NonNegativeQuadraticOptimizer();                
         	return new GenericItemBasedRecommender(model, similarity);
            }
        };
 
        // Recommend certain number of items for a particular user
        // Here, recommending 5 items to user_id = 9
        Recommender recommender = recommenderBuilder.buildRecommender(model);
        List<RecommendedItem> recomendations = recommender.recommend(9, 5); // recommend (user_id, number_of_items_to_recommend)
        for (RecommendedItem recommendedItem : recomendations) {
            System.out.println(recommendedItem);    
        }
        System.out.println();
        System.out.println("Evaluation:");
        
    RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();        
    double score = evaluator.evaluate(recommenderBuilder, null, model, 0.7, 1.0);    
    System.out.println("RMSE: " + score);
        
        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();        
        IRStatistics stats = statsEvaluator.evaluate(recommenderBuilder, null, model, null, 10, 4, 0.7); // evaluate precision recall at 10
        
    System.out.println("Precision: " + stats.getPrecision());
    System.out.println("Recall: " + stats.getRecall());
    System.out.println("F1 Score: " + stats.getF1Measure());                
    }
}