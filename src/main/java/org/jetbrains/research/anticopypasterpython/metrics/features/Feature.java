package org.jetbrains.research.anticopypasterpython.metrics.features;

public enum Feature {
    //Meta-Features
    TotalLinesOfCode("TotalLinesOfCode", 0),
    TotalSymbols("TotalSymbols", 1),
    SymbolsPerLine("SymbolsPerLine", 2),
    Area("Area", 3),
    AreaPerLine("AreaPerLine", 4),

    //Coupling-Features
    TotalConnectivity("TotalConnectivity", 5),
    TotalConnectivityPerLine("TotalConnectivityPerLine", 6),
    FieldConnectivity("FieldConnectivity", 7),
    FieldConnectivityPerLine("FieldConnectivityPerLine", 8),
    MethodConnectivity("MethodConnectivity", 9),
    MethodConnectivityPerLine("MethodConnectivityPerLine", 10),

    //Method-Features
    MethodDeclarationLines("MethodDeclarationLines", 11),
    MethodDeclarationSymbols("MethodDeclarationSymbols", 12),
    MethodDeclarationSymbolsPerLine("MethodDeclarationSymbolsPerLine", 13),
    MethodDeclarationArea("MethodDeclarationArea", 14),
    MethodDeclarationAreaPerLine("MethodDeclarationDepthPerLine", 15),

    //Keyword-Features (update for Python)
    KeywordFalseTotalCount("KeywordFalseTotalCount", 16),
    KeywordFalseCountPerLine("KeywordFalseCountPerLine", 17),
    KeywordNoneTotalCount("KeywordNoneTotalCount", 18),
    KeywordNoneCountPerLine("KeywordNoneCountPerLine", 19),
    KeywordTrueCountPerLine("KeywordTrueCountPerLine", 21),
    KeywordAndTotalCount("KeywordAndTotalCount", 22),
    KeywordAndCountPerLine("KeywordAndCountPerLine", 23),
    KeywordAsTotalCount("KeywordAsTotalCount", 24),
    KeywordAsCountPerLine("KeywordAsCountPerLine", 25),
    KeywordAssertTotalCount("KeywordAssertTotalCount", 26),
    KeywordAssertCountPerLine("KeywordAssertCountPerLine", 27),
    KeywordAsyncTotalCount("KeywordAsyncTotalCount", 28),
    KeywordAsyncCountPerLine("KeywordAsyncCountPerLine", 29),
    KeywordAwaitTotalCount("KeywordAwaitTotalCount", 30),
    KeywordAwaitCountPerLine("KeywordAwaitCountPerLine", 31),
    KeywordBreakTotalCount("KeywordBreakTotalCount", 32),
    KeywordBreakCountPerLine("KeywordBreakCountPerLine", 33),
    KeywordClassTotalCount("KeywordClassTotalCount", 34),
    KeywordClassCountPerLine("KeywordClassCountPerLine", 35),
    KeywordContinueTotalCount("KeywordContinueTotalCount", 36),
    KeywordContinueCountPerLine("KeywordContinueCountPerLine", 37),
    KeywordDefTotalCount("KeywordDefTotalCount", 38),
    KeywordDefCountPerLine("KeywordDefCountPerLine", 39),
    KeywordDelTotalCount("KeywordDelTotalCount", 40),
    KeywordDelCountPerLine("KeywordDelCountPerLine", 41),
    KeywordElifTotalCount("KeywordElifTotalCount", 42),
    KeywordElifCountPerLine("KeywordElifCountPerLine", 43),
    KeywordElseTotalCount("KeywordElseTotalCount", 44),
    KeywordElseCountPerLine("KeywordElseCountPerLine", 45),
    KeywordExceptTotalCount("KeywordExceptTotalCount", 46),
    KeywordExceptCountPerLine("KeywordExceptCountPerLine", 47),
    KeywordFinallyTotalCount("KeywordFinallyTotalCount", 48),
    KeywordFinallyCountPerLine("KeywordFinallyCountPerLine", 49),
    KeywordForTotalCount("KeywordForTotalCount", 50),
    KeywordForCountPerLine("KeywordForCountPerLine", 51),
    KeywordFromTotalCount("KeywordFromTotalCount", 52),
    KeywordFromCountPerLine("KeywordFromCountPerLine", 53),
    KeywordGlobalTotalCount("KeywordGlobalTotalCount", 54),
    KeywordGlobalCountPerLine("KeywordGlobalCountPerLine", 55),
    KeywordIfTotalCount("KeywordIfTotalCount", 56),
    KeywordIfCountPerLine("KeywordIfCountPerLine", 57),
    KeywordImportTotalCount("KeywordImportTotalCount", 58),
    KeywordImportCountPerLine("KeywordImportCountPerLine", 59),
    KeywordInTotalCount("KeywordInTotalCount", 60),
    KeywordInCountPerLine("KeywordInCountPerLine", 61),
    KeywordIsTotalCount("KeywordIsTotalCount", 62),
    KeywordIsCountPerLine("KeywordIsCountPerLine", 63),
    KeywordLambdaTotalCount("KeywordLambdaTotalCount", 64),
    KeywordLambdaCountPerLine("KeywordLambdaCountPerLine", 65),
    KeywordNonlocalTotalCount("KeywordNonlocalTotalCount", 66),
    KeywordNonlocalCountPerLine("KeywordNonlocalCountPerLine", 67),
    KeywordNotTotalCount("KeywordNotTotalCount", 68),
    KeywordNotCountPerLine("KeywordNotCountPerLine", 69),
    KeywordOrTotalCount("KeywordOrTotalCount", 70),
    KeywordOrCountPerLine("KeywordOrCountPerLine", 71),
    KeywordPassTotalCount("KeywordPassTotalCount", 72),
    KeywordPassCountPerLine("KeywordPassCountPerLine", 73),
    KeywordRaiseTotalCount("KeywordRaiseTotalCount", 74),
    KeywordRaiseCountPerLine("KeywordRaiseCountPerLine", 75),
    KeywordReturnTotalCount("KeywordReturnTotalCount", 76),
    KeywordReturnCountPerLine("KeywordReturnCountPerLine", 77),
    KeywordTryTotalCount("KeywordTryTotalCount", 78),
    KeywordTryCountPerLine("KeywordTryCountPerLine", 79),
    KeywordWhileTotalCount("KeywordWhileTotalCount", 80),
    KeywordWhileCountPerLine("KeywordWhileCountPerLine", 81),
    KeywordWithTotalCount("KeywordWithTotalCount", 82),
    KeywordWithCountPerLine("KeywordWithCountPerLine", 83),
    KeywordYieldTotalCount("KeywordYieldTotalCount", 84),
    KeywordYieldCountPerLine("KeywordYieldCountPerLine", 85);

    private final String name;
    private final int id;

    Feature(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public static Feature fromId(int id) {
        return Feature.values()[id];
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        if (name.startsWith("Keyword") && name.endsWith("TotalCount")) {
            return "the total count of the " + name.substring(7, 7 + name.length() - "Keyword".length() - "TotalCount".length()) + " keyword";
        }

        if (name.startsWith("Keyword") && name.endsWith("CountPerLine")) {
            return "the average count of the " + name.substring(7, 7 + name.length() - "Keyword".length() - "CountPerLine".length()) + " keyword";
        }

        return switch (this) {
            case MethodDeclarationSymbols -> "the total size of the enclosing method in symbols";
            case MethodDeclarationSymbolsPerLine -> "the per-line-averaged size of the enclosing method in symbols";
            case MethodDeclarationArea -> "the total nesting area of the enclosing method";
            case MethodDeclarationAreaPerLine -> "the per-line-averaged nesting area of the enclosing method";
            case TotalSymbols -> "the total size of the code fragment in symbols";
            case SymbolsPerLine -> "the per-line-averaged size of the code fragment in symbols";
            case Area -> "the total nested area of the code fragment";
            case AreaPerLine -> "the per-line-averaged nested area of the code fragment";
            case TotalLinesOfCode -> "the total number of lines of code";
            case TotalConnectivity -> "the total coupling with the enclosing class";
            case TotalConnectivityPerLine -> "the average coupling with the enclosing class";
            case FieldConnectivity -> "the total coupling with the enclosing class by fields";
            case FieldConnectivityPerLine -> "the average coupling with the enclosing class by fields";
            case MethodConnectivity -> "the total coupling with the enclosing class by methods";
            case MethodConnectivityPerLine -> "the average coupling with the enclosing class by methods";
            default -> "";
        };
    }

    public int getId() {
        return id;
    }
}

