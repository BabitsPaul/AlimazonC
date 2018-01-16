package ai_drones;

import misc.Request;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SearchRequestAnalysis
{
	public void runIteration(List<Request> requests, boolean dumpSummary)
		throws Exception
	{
		Instances instances = toInstances(requests);

		Classifier cModel = new NaiveBayes();
		cModel.buildClassifier(instances);

		Evaluation eTest = new Evaluation(instances);
		eTest.evaluateModel(cModel, instances);

		if(dumpSummary)
			System.out.println(eTest.toSummaryString());
	}

	private void storeData()
	{

	}

	private Instances toInstances(List<Request> l)
	{
		// just one instance here, only the request-strings need to be classified
		Attribute requestAttr = new Attribute("request", true);

		ArrayList<Attribute> attrs = new ArrayList<>();
		attrs.add(requestAttr);

		Instances res = new Instances("request set", attrs, l.size());

		for(Request r : l)
		{
			Instance instance = new DenseInstance(1);
			instance.setValue(0, r.getRequest());

			res.add(instance);
		}

		return res;
	}
}
