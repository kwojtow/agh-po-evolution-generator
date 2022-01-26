package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Genes {
    private ArrayList<Integer> genesSet = new ArrayList<>();
    private Random generator = new Random();

    /**
     * Generates random set of genes
     */
    Genes() {
        Random generator = new Random();
        for (int i = 0; i < 8; i++) {
            getGenesSet().add(i);
        }
        for (int i = 8; i < 32; i++) {
            getGenesSet().add(generator.nextInt(8));
        }
        getGenesSet().sort((x, y) -> x - y);
    }

    /**
     * Generates set of genes basing on two animals genes
     * @param mothersGenes first animal to base on
     * @param fathersGenes second animal to base on
     */
    Genes(Genes mothersGenes, Genes fathersGenes) {
        int[] genesSplitPoints = new int[4];

        genesSplitPoints[0] = 0;
        genesSplitPoints[1] = generator.nextInt(31) + 1;
        genesSplitPoints[2] = generator.nextInt(31) + 1;
        genesSplitPoints[3] = 32;
        while (genesSplitPoints[1] == genesSplitPoints[2]) {
            genesSplitPoints[2] = generator.nextInt(32);
        }
        if (genesSplitPoints[1] > genesSplitPoints[2]) {
            int tmp = genesSplitPoints[1];
            genesSplitPoints[1] = genesSplitPoints[2];
            genesSplitPoints[2] = tmp;
        }

        int motherPart = generator.nextInt(3);

        this.getGenesSet().addAll(fathersGenes.getGenesSet());
        this.getGenesSet().subList(genesSplitPoints[motherPart], genesSplitPoints[motherPart + 1]).clear();
        this.getGenesSet().addAll(mothersGenes.getGenesSet().subList(genesSplitPoints[motherPart], genesSplitPoints[motherPart + 1]));

        completeMissingGenes(this.getGenesSet());
        getGenesSet().sort((x, y) -> x - y);

    }

    /**
     * Complete the missing genes in genes set
     * @param genesSetToComplete genes set to complete missing
     */
    void completeMissingGenes(List<Integer> genesSetToComplete) {
        boolean genesOK;
        do {
            genesOK = true;
            for (int i = 0; i < 8; i++) {
                if (!genesSetToComplete.contains(i)) {
                    genesSetToComplete.remove(generator.nextInt(32));
                    genesSetToComplete.add(i);
                    genesOK = false;
                }
            }
        } while (!genesOK);
    }

    /**
     * Compares object to given
     * @param o object to compare with
     * @return returns true when object equals, false in other case
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genes genes = (Genes) o;
        return getGenesSet().equals(genes.getGenesSet());
    }

    /**
     * Method calculating hash code of object
     * @return returns the hach code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getGenesSet());
    }

    /**
     * Method returns genes set as string
     * @return returns string of single genes
     */
    @Override
    public String toString() {
        return getGenesSet().stream().map((gene) -> String.valueOf(gene)).reduce((gene1, gene2) -> gene1 + gene2).get();
    }

    public ArrayList<Integer> getGenesSet() {
        return genesSet;
    }
}
