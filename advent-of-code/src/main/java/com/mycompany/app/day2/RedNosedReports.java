package com.mycompany.app.day2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * --- Day 2: Red-Nosed Reports ---
 * Fortunately, the first location The Historians want to search isn't a long walk from the Chief Historian's office.
 * <p>
 * While the Red-Nosed Reindeer nuclear fusion/fission plant appears to contain no sign of the Chief Historian, the engineers there run up to you as soon as they see you. Apparently, they still talk about the time Rudolph was saved through molecular synthesis from a single electron.
 * <p>
 * They're quick to add that - since you're already here - they'd really appreciate your help analyzing some unusual data from the Red-Nosed reactor. You turn to check if The Historians are waiting for you, but they seem to have already divided into groups that are currently searching every corner of the facility. You offer to help with the unusual data.
 * <p>
 * The unusual data (your puzzle input) consists of many reports, one report per line. Each report is a list of numbers called levels that are separated by spaces. For example:
 * <p>
 * 7 6 4 2 1
 * 1 2 7 8 9
 * 9 7 6 2 1
 * 1 3 2 4 5
 * 8 6 4 4 1
 * 1 3 6 7 9
 * This example data contains six reports each containing five levels.
 * <p>
 * The engineers are trying to figure out which reports are safe. The Red-Nosed reactor safety systems can only tolerate levels that are either gradually increasing or gradually decreasing. So, a report only counts as safe if both of the following are true:
 * <p>
 * The levels are either all increasing or all decreasing.
 * Any two adjacent levels differ by at least one and at most three.
 * In the example above, the reports can be found safe or unsafe by checking those rules:
 * <p>
 * 7 6 4 2 1: Safe because the levels are all decreasing by 1 or 2.
 * 1 2 7 8 9: Unsafe because 2 7 is an increase of 5.
 * 9 7 6 2 1: Unsafe because 6 2 is a decrease of 4.
 * 1 3 2 4 5: Unsafe because 1 3 is increasing but 3 2 is decreasing.
 * 8 6 4 4 1: Unsafe because 4 4 is neither an increase or a decrease.
 * 1 3 6 7 9: Safe because the levels are all increasing by 1, 2, or 3.
 * So, in this example, 2 reports are safe.
 * <p>
 * Analyze the unusual data from the engineers. How many reports are safe?
 * <p>
 * Your puzzle answer was 585.
 * <p>
 * The first half of this puzzle is complete! It provides one gold star: *
 * <p>
 * --- Part Two ---
 * The engineers are surprised by the low number of safe reports until they realize they forgot to tell you about the Problem Dampener.
 * <p>
 * The Problem Dampener is a reactor-mounted module that lets the reactor safety systems tolerate a single bad level in what would otherwise be a safe report. It's like the bad level never happened!
 * <p>
 * Now, the same rules apply as before, except if removing a single level from an unsafe report would make it safe, the report instead counts as safe.
 * <p>
 * More of the above example's reports are now safe:
 * <p>
 * 7 6 4 2 1: Safe without removing any level.
 * 1 2 7 8 9: Unsafe regardless of which level is removed.
 * 9 7 6 2 1: Unsafe regardless of which level is removed.
 * 1 3 2 4 5: Safe by removing the second level, 3.
 * 8 6 4 4 1: Safe by removing the third level, 4.
 * 1 3 6 7 9: Safe without removing any level.
 * Thanks to the Problem Dampener, 4 reports are actually safe!
 * <p>
 * Update your analysis by handling situations where the Problem Dampener can remove a single level from unsafe reports. How many reports are now safe?
 * <p>
 * Your puzzle answer was 626.
 */
public class RedNosedReports {

    private static final String FILE_PATH = "/red_nosed_reports.txt";

    public static void main(final String[] args) throws IOException {
        final RedNosedReports historianHysteria = new RedNosedReports();
        System.out.println("Total Safe Reports: " + historianHysteria.getSafeReportsAmount(false));
        System.out.println("Total Safe Reports: " + historianHysteria.getSafeReportsAmount(true));
    }

    int getSafeReportsAmount(boolean allowMistake) throws IOException {
        final InputStream inputStream = this.getClass().getResourceAsStream(FILE_PATH);
        return getSafeReportsAmount(inputStream, allowMistake);
    }

    private int getSafeReportsAmount(final InputStream inputStream, boolean allowMistake) throws IOException {
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            int safeCount = 0;
            String line;
            while ((line = br.readLine()) != null) {
                boolean isSafe = isLineSafe(line, allowMistake);
                if (isSafe) {
                    safeCount++;
                }
            }
            return safeCount;
        }
    }

    private boolean isLineSafe(String line, boolean allowMistake) {
        Iterator<String> iterator = Arrays.stream(line.trim().split(" ")).iterator();
        boolean asc = false;
        boolean desc = false;
        String current = iterator.next();
        int currentIndex = 0;
        int nextIndex = 0;
        while (iterator.hasNext()) {
            String next = iterator.next();
            nextIndex++;
            int currentInt = Integer.parseInt(current);
            int nextInt = Integer.parseInt(next);

            if (!asc && !desc) {
                if (currentInt < nextInt) {
                    asc = true;
                } else {
                    desc = true;
                }
            }

            int diff = currentInt - nextInt;
            if (!isAscending(asc, diff) && !isDescending(desc, diff)) {
                if (allowMistake) {
                    // create line without current and try again
                    String lineWithoutCurrent = removeLevelFromLine(line, currentIndex);

                    // create line without next and try again
                    String lineWithoutNext = removeLevelFromLine(line, nextIndex);
                    boolean isSafe = isLineSafe(lineWithoutCurrent, false) || isLineSafe(lineWithoutNext, false);
                    if (!isSafe && currentIndex > 0) {
                        // create line without current and try again
                        String lineWithoutPrevious = removeLevelFromLine(line, currentIndex - 1);
                        return isLineSafe(lineWithoutPrevious, false);
                    }
                } else {
                    return false;
                }
            }
            current = next;
            currentIndex++;
        }
        return true;
    }

    private static boolean isAscending(boolean asc, int diff) {
        return asc && diff <= -1 && diff >= -3;
    }

    private boolean isDescending(boolean desc, int diff) {
        return desc && 1 <= diff && diff <= 3;
    }

    private String removeLevelFromLine(String line, int index) {
        List<String> lineWithoutIndexAsList = Arrays.stream(line.split(" ")).collect(Collectors.toList());
        lineWithoutIndexAsList.remove(index);
        return String.join(" ", lineWithoutIndexAsList);
    }
}
