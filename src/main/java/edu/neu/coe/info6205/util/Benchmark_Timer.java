/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.simple.InsertionSort;
import edu.neu.coe.info6205.union_find.UF_HWQUPC;
import edu.neu.coe.info6205.union_find.UF_HWQUPC_alternative;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);

    public static void main(String[] args) {
        Random r = new Random();
        int m = 50;   // number of runs
        int a = 1000;
        int b = 1000; // length for ordered array
        int c = 1000; // length for reverse ordered array
        int d = 1000; // length for partially ordered array
        int x = 1000;

        for(int k = 0 ; k < 5; k++) {
            a = a * 2;
            Consumer<Integer> consumer = number -> {
                UF_HWQUPC.countPairs(number);
            };
            Benchmark_Timer<Integer> benchTimer = new Benchmark_Timer<>("Number of nodes " + a, consumer);
            int N = a;
            Supplier<Integer> integerSupplier = () -> N;
            consumer.accept(integerSupplier.get());
            System.out.println(benchTimer.run(integerSupplier.get(),m));
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        for(int k = 0 ; k < 5; k++) {
            x = x * 2;
            Consumer<Integer> consumer = number -> {
                UF_HWQUPC_alternative.countPairs(number);
            };
            Benchmark_Timer<Integer> benchTimer = new Benchmark_Timer<>("Number of nodes " + a, consumer);
            int N = x;
            Supplier<Integer> integerSupplier = () -> N;
            consumer.accept(integerSupplier.get());
            System.out.println(benchTimer.run(integerSupplier.get(),m));
        }

        /**
         * Timing the randomly ordered array
         * for e.g : {1,3,2,1,7,2,....N}
         * returns time for randomly ordered array
         */
        for (int k = 0; k < 5; k++) {
            a = a * 2;
            InsertionSort<Integer> iSort = new InsertionSort<>();
            Consumer<Integer[]> consumer = array -> iSort.sort(array, 0, array.length);
            Benchmark_Timer<Integer[]> benchTimer = new Benchmark_Timer<>("Insertion sort for randomArray with array length : " + a, consumer);
            int N = a;
            Supplier<Integer[]> randomSupplier = () -> {
                Integer[] array = new Integer[N];

                for (int i = 0; i < N; i++)
                    array[i] = r.nextInt();
                return array;
            };
            consumer.accept(randomSupplier.get());
            System.out.println(benchTimer.run(randomSupplier.get(), m));
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");

        /**
         * Timing the ordered array
         * for e.g : {1,2,3,....N}
         * returns time for ordered array
         */
        for (int k = 0; k < 5; k++) {
            b = b * 2;
            InsertionSort<Integer> iSort = new InsertionSort<>();
            Consumer<Integer[]> consumer = array -> iSort.sort(array, 0, array.length);
            Benchmark_Timer<Integer[]> benchTimer = new Benchmark_Timer<>("Insertion sort for orderedArray with array length : " + b, consumer);
            int N = b;

            Supplier<Integer[]> orderedSupplier = () -> {
                Integer[] array = new Integer[N];

                for (int i = 0; i < N; i++)
                    array[i] = i;
                return array;
            };
            consumer.accept(orderedSupplier.get());
            System.out.println(benchTimer.run(orderedSupplier.get(), m));
        }
        /**
         * Timing the reverse array
         * for e.g : {N,N-1,N-2,......,1}
         * returns time for reverse array
         */
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        for (int k = 0; k < 5; k++) {
            c = c * 2;
            InsertionSort<Integer> iSort = new InsertionSort<>();
            Consumer<Integer[]> consumer = array -> iSort.sort(array, 0, array.length);
            Benchmark_Timer<Integer[]> benchTimer = new Benchmark_Timer<>("Insertion sort for reverseArray with array length : " + c, consumer);
            int N = c;

            Supplier<Integer[]> reverseSupplier = () -> {
                Integer[] array = new Integer[N];

                for (int i = N - 1; i >= 0; i--)
                    array[i] = i;
                return array;
            };
            consumer.accept(reverseSupplier.get());
            System.out.println(benchTimer.run(reverseSupplier.get(), m));
        }
        /**
         * Timing the partially ordered array
         * for e.g : {1,2,3,N/2,1,2,3....N}
         * returns time for partially ordered array
         */
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        for (int k = 0; k < 5; k++) {
            d = d * 2;
            InsertionSort<Integer> iSort = new InsertionSort<>();
            Consumer<Integer[]> consumer = array -> iSort.sort(array, 0, array.length);
            Benchmark_Timer<Integer[]> benchTimer = new Benchmark_Timer<>("Insertion sort for partialArray with array length : " + d, consumer);
            int N = d;

            Supplier<Integer[]> partialOrderedSupplier = () -> {
                Integer[] array = new Integer[N];

                for (int i = 0; i < N / 2; i++)
                    array[i] = i;
                for (int j = N / 2; j < N; j++)
                    array[j] = r.nextInt();
                return array;
            };
            consumer.accept(partialOrderedSupplier.get());
            System.out.println(benchTimer.run(partialOrderedSupplier.get(), m));
        }
    }
}