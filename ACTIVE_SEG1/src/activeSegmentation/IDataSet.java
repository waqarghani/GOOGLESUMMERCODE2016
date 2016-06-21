package activeSegmentation;

import weka.core.Instance;
import weka.core.Instances;

public interface IDataSet {
	
	 /**
     * @return The dataset in Weka format
     */
    public Instances getDataset();

    /**
     * @return The number of attributes of the dataset
     */
    public int getNumAttributes();

    /**
     * @return A copy of the current dataset
     */
    public IDataSet copy();

    /**
     * Add to the current dataset the dataset passed as argument
     *
     * @param dataset The dataset to addAll
     */
    public void addAll(IDataSet dataset);

    /**
     * @return The number of instances of the dataset
     */
    public int getNumInstances();

    /**
     * @param index The index of the instance
     * @return The instance
     */
    public Instance instance(int index);

    /**
     * Set the instance in the specified position
     *
     * @param index The index of the position
     * @param instance The instance to set in the position
     */
    public void set(int index, Instance instance);

    /**
     *
     * @return True if the dataset is empty, false otherwise.
     */
    public boolean isEmpty();

    /**
     * Add an instance to the dataset
     *
     * @param instance The instance to add
     */
    public void add(Instance instance);

    /**
     * Remove an instance from dataset
     *
     * @param index The index of the instance to remove
     */
    public void remove(int index);

    /**
     * Removes all elements
     */
    public void delete();

}
