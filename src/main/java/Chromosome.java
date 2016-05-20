import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {

    /**
     * The list of cities, which are the genes of this chromosome.
     */
    protected int[] cityList;

    /**
     * The cost of following the cityList order of this chromosome.
     */
    protected double cost;

    /**
     * @param cities The order that this chromosome would visit the cities.
     */
    Chromosome(City[] cities) {
        Random generator = new Random();
        cityList = new int[cities.length];
        //cities are visited based on the order of an integer representation [o,n] of each of the n cities.
        for (int x = 0; x < cities.length; x++) {
            cityList[x] = x;
        }

        //shuffle the order so we have a random initial order
        for (int y = 0; y < cityList.length; y++) {
            int temp = cityList[y];
            int randomNum = generator.nextInt(cityList.length);
            cityList[y] = cityList[randomNum];
            cityList[randomNum] = temp;
        }

        calculateCost(cities);
    }

    /**
     * @param cities Utilised to calculate the chromosome cost
     * @param cityList The order that the chromosome is to traverse the cities
     */
    Chromosome(City[]cities, int[] cityList)
    {
        this.cityList = new int[cities.length];

        //set the new values of the city list
        setCities(cityList);
        calculateCost(cities);
    }

    /**
     * Calculate the cost of the specified list of cities.
     *
     * @param cities A list of cities.
     */
    void calculateCost(City[] cities) {
        cost = 0;
        for (int i = 0; i < cityList.length - 1; i++) {
            double dist = cities[cityList[i]].proximity(cities[cityList[i + 1]]);
            cost += dist;
        }

        cost += cities[cityList[0]].proximity(cities[cityList[cityList.length - 1]]); //Adding return home
    }

    /**
     * Get the cost for this chromosome. This is the amount of distance that
     * must be traveled.
     */
    double getCost() {
        return cost;
    }

    /**
     * @param i The city you want.
     * @return The ith city.
     */
    int getCity(int i) {
        return cityList[i];
    }

    /**
     * Set the order of cities that this chromosome would visit.
     *
     * @param list A list of cities.
     */
    void setCities(int[] list) {
        for (int i = 0; i < cityList.length; i++) {
            cityList[i] = list[i];
        }
    }

    /**
     * Set the index'th city in the city list.
     *
     * @param index The city index to change
     * @param value The city number to place into the index.
     */
    void setCity(int index, int value) {
        cityList[index] = value;
    }

    /**
     * Generates mutated offspring of chromosome using Inversion
     * @param cities list of cities
     * @return The mutated offspring
     */
    Chromosome mutateWithInversion(City[] cities)
    {
        Random generator = new Random();

        int[] twoPoints = new int[2];
        twoPoints[0] = twoPoints[1] = 0;

        //get two unique values
        while(twoPoints[0] == twoPoints[1])
        {
            twoPoints[0] = generator.nextInt(cityList.length);
            twoPoints[1] = generator.nextInt(cityList.length);
        }
        //sort the values in ascending order
        Arrays.sort(twoPoints);

        int[] mutantCityList = Arrays.copyOf(cityList, cityList.length);
        int segmentLength = (twoPoints[1]-twoPoints[0])+1;
        int[] segment = new int[segmentLength];

        for(int turn = 0; turn < 2; turn++)
        {
            if(turn == 1)
            {
                //reverse the segment
                for(int i = 0; i < segment.length / 2; i++)
                {
                    int temp = segment[i];
                    segment[i] = segment[segment.length - i - 1];
                    segment[segment.length - i - 1] = temp;
                }
            }

            for(int i = 0; i < segmentLength; i++)
            {
                if(turn == 0)
                {
                    //get the items for the segment
                    segment[i] = mutantCityList[(twoPoints[0]+i)];
                }
                else
                {
                    //fill into the list the reverse items
                    mutantCityList[(twoPoints[0]+i)] = segment[i];
                }
            }
        }

        Chromosome mutant = new Chromosome(cities, mutantCityList);
        return mutant;
    }

    /**
     * Creates mutated offspring of chromosome using 3 Point Exchange
     * @param cities list of cities
     * @return The mutated offspring
     */
    Chromosome mutateWith3Point(City[] cities)
    {
        Random generator = new Random();

        //using 3-point exchange
        int[] threePoints = new int[3];
        threePoints[0] = threePoints[1] = threePoints[2] = 0;

        // get three unique values
        while((threePoints[0] == threePoints[1]) || (threePoints[0] == threePoints[2]) || (threePoints[1] == threePoints[2]))
        {
            threePoints[0] = generator.nextInt(cityList.length+1);
            threePoints[1] = generator.nextInt(cityList.length+1);
            threePoints[2] = generator.nextInt(cityList.length+1);
        }
        // sort values in ascending order
        Arrays.sort(threePoints);
        ArrayList<Integer> tempArr = new ArrayList<Integer>();

        int i = 0;
        while(true)
        {
            // if the start point, move to the point after the mid point
            if(i == threePoints[0])
            {
                i = threePoints[1];
            }
            else
            {
                if(i == threePoints[2])
                {
                    //if last point, add items from start to mid point to collection
                    for(int y = threePoints[0]; y <= threePoints[1]; y++)
                    {
                        tempArr.add(cityList[y]);
                    }
                }
                //to cater for scenarios when the third point is 50
                if(i < cityList.length)
                    tempArr.add(cityList[i]);
            }
            i++;

            if(i > cityList.length)
                break;
        }

        int[] mutantCityList = new int[cityList.length];
        for(int j = 0; j < mutantCityList.length; j++)
        {
            mutantCityList[j] = tempArr.get(j);
        }

        Chromosome mutant = new Chromosome(cities, mutantCityList);
        return mutant;
    }

    /**
     * Compares two chromosomes based on their costs
     * @param other chromosome to compare against
     * @return 0 if equal, < 0 if other chromosome is greater and > 0 if other chromosome is lesser
     */
    public int compareTo(Chromosome other)
    {
        return Double.compare(this.cost, other.cost);
    }
    /**
     * Sort the chromosomes by their cost.
     *
     * @param chromosomes An array of chromosomes to sort.
     * @param num         How much of the chromosome list to sort.
     */
    public static void sortChromosomes(Chromosome chromosomes[], int num) {
        Chromosome ctemp;
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 0; i < num - 1; i++) {
                if (chromosomes[i].getCost() > chromosomes[i + 1].getCost()) {
                    ctemp = chromosomes[i];
                    chromosomes[i] = chromosomes[i + 1];
                    chromosomes[i + 1] = ctemp;
                    swapped = true;
                }
            }
        }
    }
}
