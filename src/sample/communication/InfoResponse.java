package sample.communication;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tobia on 14.01.2017.
 */
public class InfoResponse {
    @SerializedName("average_clustering") double averageClustering;
    @SerializedName("avg_node_degree") double averageNodeDegree;
    @SerializedName("degree_assortativity_coefficient") double assortativityCoefficient;
    @SerializedName("degree_pearson_correlation_coefficient") double pearsoonCorrelationCoefficient;
    @SerializedName("number_of_edges") int numberOfEdges;
    @SerializedName("number_of_nodes") int numberOfNodes;
    @SerializedName("pearson_corelation") double pearsonCorelation;
    @SerializedName("transitivity") double transitivity;

    public double getAverageClustering() {
        return averageClustering;
    }

    public void setAverageClustering(double averageClustering) {
        this.averageClustering = averageClustering;
    }

    public double getAverageNodeDegree() {
        return averageNodeDegree;
    }

    public void setAverageNodeDegree(double averageNodeDegree) {
        this.averageNodeDegree = averageNodeDegree;
    }

    public double getAssortativityCoefficient() {
        return assortativityCoefficient;
    }

    public void setAssortativityCoefficient(double assortativityCoefficient) {
        this.assortativityCoefficient = assortativityCoefficient;
    }

    public double getPearsoonCorrelationCoefficient() {
        return pearsoonCorrelationCoefficient;
    }

    public void setPearsoonCorrelationCoefficient(double pearsoonCorrelationCoefficient) {
        this.pearsoonCorrelationCoefficient = pearsoonCorrelationCoefficient;
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public double getPearsonCorelation() {
        return pearsonCorelation;
    }

    public void setPearsonCorelation(double pearsonCorelation) {
        this.pearsonCorelation = pearsonCorelation;
    }

    public double getTransitivity() {
        return transitivity;
    }

    public void setTransitivity(double transitivity) {
        this.transitivity = transitivity;
    }
}
