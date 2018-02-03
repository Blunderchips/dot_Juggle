//RELEASE-STATUS: DIST
/**
 * <p>
 * Miscellaneaous utility classes.
 * <p>
 * <h2>Pseudorandom number generators</h2>
 * <p>
 * <p>
 * We provide a number of fast, high-quality PRNGs with different features. As a
 * general review:
 * <ul>
 * <li>{@link it.unimi.dsi.util.SplitMix64Random SplitMix64Random} is the
 * fastest generator, but it has a relatively short period (2<sup>64</sup>) so
 * it should not be used to generate very long sequences (the rule of thumb to
 * have a period greater than the square of the length of the sequence you want
 * to generate). It is a non-splittable version of Java 8's
 * <a href="http://docs.oracle.com/javase/8/docs/api/java/util/SplittableRandom.html"><code>SplittableRandom</code></a>.
 * <li>{@link it.unimi.dsi.util.XorShift128PlusRandom XorShift128PlusRandom} is
 * a fast-as-light, all-purpose top-quality generator. It is slightly slower
 * than {@link it.unimi.dsi.util.SplitMix64Random SplitMix64Random}, but its
 * period (2<sup>128</sup> &minus; 1) is sufficient for any single-thread
 * application.
 * <li>{@link it.unimi.dsi.util.XorShift1024StarRandom XorShift1024StarRandom}
 * is fast and provides a long period (2<sup>1024</sup> &minus; 1) for massive
 * parallel computations.
 * <li>{@link it.unimi.dsi.util.XorShift64StarRandom XorShift64StarRandom} is
 * mainly of historical interest, as
 * {@link it.unimi.dsi.util.SplitMix64Random SplitMix64Random} is better under
 * every respect.
 * </ul>
 * <p>
 * <p>
 * A table summarizing timings is provided below. Note that we test several
 * different method parameters to show the large gap between full 64-bit
 * generators and {@link java.util.Random}.
 * <p>
 * <CENTER><TABLE SUMMARY="Timings in nanoseconds for several generators" BORDER=1>
 * <TR><TH>
 * <TH><a href="http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ThreadLocalRandom.html"><code>ThreadLocalRandom</code></a>
 * <TH><a href="http://docs.oracle.com/javase/8/docs/api/java/util/SplittableRandom.html"><code>SplittableRandom</code></a>
 * <TH>{@link it.unimi.dsi.util.SplitMix64RandomGenerator SplitMix64RandomGenerator}
 * <TH>{@link it.unimi.dsi.util.XorShift128PlusRandom XorShift128PlusRandom}
 * <TH>{@link it.unimi.dsi.util.XorShift64StarRandom XorShift64StarRandom}
 * <TH>{@link it.unimi.dsi.util.XorShift1024StarRandom XorShift1024StarRandom}
 * <TH><a href="http://maths-people.anu.edu.au/~brent/random.html"><code>xorgens</code></a>
 * <TR><TD> nextInt()       <TD>1.19<TD>1.23<TD>1.17<TD>1.42<TD>1.70<TD>2.16<TD>2.64
 * <TR><TD> nextLong()      <TD>1.16<TD>1.23<TD>1.16<TD>1.32<TD>1.62<TD>2.06<TD>2.32
 * <TR><TD> nextDouble()
 * <TD>2.06<TD>2.07<TD>2.06<TD>2.06<TD>2.06<TD>2.13<TD>2.61
 * <TR><TD> nextInt(1000000)
 * <TD>3.06<TD>2.67<TD>2.21<TD>2.79<TD>2.34<TD>3.27<TD>3.75
 * <TR><TD> nextInt(2^29+2^28)
 * <TD>8.08<TD>6.79<TD>2.53<TD>2.86<TD>2.42<TD>3.53<TD>4.10
 * <TR><TD> nextInt(2^30)
 * <TD>1.24<TD>1.32<TD>1.58<TD>2.34<TD>1.80<TD>2.68<TD>2.98
 * <TR><TD> nextInt(2^30+1)
 * <TD>16.00<TD>13.37<TD>2.37<TD>2.87<TD>2.28<TD>3.41<TD>4.06
 * <TR><TD> nextInt(2^30+2^29)
 * <TD>8.09<TD>6.82<TD>2.52<TD>2.85<TD>2.45<TD>3.51<TD>4.02
 * <TR><TD> nextLong(1000000000000)
 * <TD>3.26<TD>2.73<TD>2.65<TD>2.63<TD>3.03<TD>3.17<TD>4.03
 * <TR><TD> nextLong(2^62+1)
 * <TD>18.54<TD>17.07<TD>14.29<TD>12.64<TD>13.16<TD>15.11<TD>16.05
 * </TABLE></CENTER>
 * <p>
 * <p>
 * The relative differences between generators are actually more marked than
 * these figures show, as the timings include the execution of a loop and of a
 * xor instruction that combines the results to avoid excision.
 * <p>
 * <p>
 * The quality of all generators we provide is very high: for instance, they
 * perform better than <code>WELL1024a</code> or <code>MT19937</code> (AKA the
 * Mersenne Twister) in the
 * <a href="http://www.iro.umontreal.ca/~simardr/testu01/tu01.html">TestU01</a>
 * BigCrush test suite. In particular,
 * {@link it.unimi.dsi.util.SplitMix64Random SplitMix64Random}, {@link it.unimi.dsi.util.XorShift128PlusRandom XorShift128PlusRandom}
 * and {@link it.unimi.dsi.util.XorShift1024StarRandom XorShift1024StarRandom}
 * pass BigCrush. More details can be found on the
 * <a href="http://xorshift.di.unimi.it/"><code>xorshift*</code>/<code>xorshift+</code>
 * generators and the PRNG shootout</a> page.
 * <p>
 * <p>
 * For each generator, we provide a version that extends
 * {@link java.util.Random}, overriding (as usual) the
 * {@link java.util.Random#next(int) next(int)} method. Nonetheless, since the
 * generators are all inherently 64-bit also {@link java.util.Random#nextInt() nextInt()}, {@link java.util.Random#nextFloat() nextFloat()},
 * {@link java.util.Random#nextLong() nextLong()}, {@link java.util.Random#nextDouble() nextDouble()}, {@link java.util.Random#nextBoolean() nextBoolean()}
 * and {@link java.util.Random#nextBytes(byte[]) nextBytes(byte[])} have been
 * overridden for speed (preserving, of course, {@link java.util.Random}'s
 * semantics).
 * <p>
 * <p>
 * If you do not need an instance of {@link java.util.Random}, or if you need a
 * {@link org.apache.commons.math3.random.RandomGenerator} to use with
 * <a href="http://commons.apache.org/math/">Commons Math</a>, there is for each
 * generator a corresponding
 * {@link org.apache.commons.math3.random.RandomGenerator RandomGenerator}
 * implementation, which indeed we suggest to use in general if you do not need
 * a generator implementing {@link java.util.Random}.
 */
// http://grepcode.com/file/repo1.maven.org/maven2/it.unimi.dsi/dsiutils/2.2.4/it/unimi/dsi/util/package-info.java?av=f
package it.unimi.dsi.util;
