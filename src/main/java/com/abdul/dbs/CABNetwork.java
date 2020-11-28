package com.abdul.dbs;

public class CABNetwork {
    public static final double[][] distance = new double[][]{
            {0, 576.9631, 946.4954, 597.5972, 373.8127, 559.7673, 709.0215, 1208.328, 603.6477, 695.208, 680.709, 1936.572, 332.4644, 592.5679, 908.7715, 426.1877, 756.1987, 672.5906, 1590.224, 527.3008, 483.4673, 2140.978, 2184.402, 408.1648, 540.7388,},
            {576.9631, 0, 369.5327, 613.0386, 429.1079, 312.8831, 1196.489, 1502.14, 405.8975, 1241.961, 960.3459, 2318.076, 786.5959, 949.5669, 938.7461, 999.5005, 179.2426, 96.2744, 1999.584, 210.7656, 736.3755, 2456.263, 2339.509, 844.1663, 36.4947,},
            {946.4954, 369.5327, 0, 858.3308, 749.6018, 556.0706, 1541.273, 1764.791, 621.3306, 1603.165, 1250.962, 2600.078, 1137.335, 1266.851, 1124.778, 1368.267, 190.3157, 274.3105, 2299.429, 494.2224, 1043.484, 2703.402, 2503.828, 1188.549, 405.7886,},
            {597.5972, 613.0386, 858.3308, 0, 255.0303, 311.3071, 790.1213, 907.4331, 237.0703, 932.2173, 406.3386, 1741.873, 485.5564, 1186.858, 345.8738, 830.3635, 720.4687, 675.3437, 1447.104, 403.8657, 255.8823, 1853.617, 1733.132, 1005.761, 592.0278,},
            {373.8127, 429.1079, 749.6018, 255.0303, 0, 225.8954, 794.1726, 1080.374, 238.944, 879.5647, 533.156, 1889.528, 402.3291, 947.3188, 598.541, 700.4368, 578.3286, 512.3965, 1570.725, 255.6551, 307.3289, 2036.128, 1967.256, 775.239, 399.2253,},
            {559.7673, 312.8831, 556.0706, 311.3071, 225.8954, 0, 1009.689, 1216.868, 94.2588, 1104.574, 694.9153, 2047.122, 627.115, 1084.5, 626.1548, 922.3181, 409.3542, 365.6853, 1743.432, 104.6478, 491.1125, 2164.855, 2027.319, 933.196, 298.8486,},
            {709.0215, 1196.489, 1541.273, 790.1213, 794.1726, 1009.689, 0, 663.8762, 982.7378, 221.422, 447.8044, 1249.763, 411.1133, 1097.608, 851.8228, 423.7053, 1362.874, 1288.966, 895.0908, 1049.266, 537.6206, 1493.843, 1686.675, 912.2104, 1161.676,},
            {1208.328, 1502.14, 1764.791, 907.4331, 1080.374, 1216.868, 663.8762, 0, 1143.791, 874.5181, 551.6299, 841.624, 880.0728, 1714.651, 694.0088, 1066.563, 1625.87, 1574.822, 593.4216, 1301.511, 780.9512, 955.802, 1024.566, 1519.174, 1475.479,},
            {603.6477, 405.8975, 621.3306, 237.0703, 238.944, 94.2588, 982.7378, 1143.791, 0, 1094.906, 636.9045, 1978.943, 620.488, 1151.868, 535.0244, 936.2502, 489.5645, 453.2583, 1682.489, 198.9058, 450.2585, 2086.845, 1936.304, 992.3379, 392.9045,},
            {695.208, 1241.961, 1603.165, 932.2173, 879.5647, 1104.574, 221.422, 874.5181, 1094.906, 0, 642.2092, 1375.635, 477.459, 963.7202, 1046.119, 305.3132, 1417.072, 1337.648, 1017.332, 1125.041, 677.0608, 1649.619, 1891.166, 795.2136, 1205.747,},
            {680.709, 960.3459, 1250.962, 406.3386, 533.156, 694.9153, 447.8044, 551.6299, 636.9045, 642.2092, 0, 1358.213, 378.5906, 1236.192, 405.0906, 674.479, 1096.712, 1038.645, 1048.539, 768.1641, 229.4867, 1506.451, 1503.794, 1038.624, 931.7148,},
            {1936.572, 2318.076, 2600.078, 1741.873, 1889.528, 2047.122, 1249.763, 841.624, 1978.943, 1375.635, 1358.213, 0, 1608.082, 2335.816, 1530.57, 1661.778, 2453.352, 2396.794, 358.3762, 2125.512, 1582.369, 361.5388, 986.8149, 2157.517, 2288.748,},
            {332.4644, 786.5959, 1137.335, 485.5564, 402.3291, 627.115, 411.1133, 880.0728, 620.488, 477.459, 378.5906, 1608.082, 0, 858.251, 700.8213, 348.2725, 955.6191, 879.9795, 1265.573, 651.1179, 254.9977, 1808.52, 1872.696, 660.5173, 751.4614,},
            {592.5679, 949.5669, 1266.851, 1186.858, 947.3188, 1084.5, 1097.608, 1714.651, 1151.868, 963.7202, 1236.192, 2335.816, 858.251, 0, 1500.774, 675.7505, 1098.282, 1021.611, 1977.613, 1015.165, 1065.599, 2591.447, 2725.79, 197.8015, 923.2229,},
            {908.7715, 938.7461, 1124.778, 345.8738, 598.541, 626.1548, 851.8228, 694.0088, 535.0244, 1046.119, 405.0906, 1530.57, 700.8213, 1500.774, 0, 1039.77, 1018.399, 987.8645, 1280.737, 728.3743, 450.3982, 1589.835, 1401.321, 1311.21, 922.3145,},
            {426.1877, 999.5005, 1368.267, 830.3635, 700.4368, 922.3181, 423.7053, 1066.563, 936.2502, 305.3132, 674.479, 1661.778, 348.2725, 675.7505, 1039.77, 0, 1178.439, 1095.657, 1304.043, 918.5615, 601.9917, 1916.578, 2090.089, 496.4224, 963.0435,},
            {756.1987, 179.2426, 190.3157, 720.4687, 578.3286, 409.3542, 1362.874, 1625.87, 489.5645, 1417.072, 1096.712, 2453.352, 955.6191, 1098.282, 1018.399, 1178.439, 0, 84.3365, 2143.565, 328.7515, 880.5469, 2574.082, 2415.489, 1008.2, 215.561,},
            {672.5906, 96.2744, 274.3105, 675.3437, 512.3965, 365.6853, 1288.966, 1574.822, 453.2583, 1337.648, 1038.645, 2396.794, 879.9795, 1021.611, 987.8645, 1095.657, 84.3365, 0, 2082.316, 273.4106, 818.1228, 2526.562, 2388.689, 926.6267, 132.7684,},
            {1590.224, 1999.584, 2299.429, 1447.104, 1570.725, 1743.432, 895.0908, 593.4216, 1682.489, 1017.332, 1048.539, 358.3762, 1265.573, 1977.613, 1280.737, 1304.043, 2143.565, 2082.316, 0, 1814.83, 1264.193, 661.6543, 1129.327, 1800.098, 1968.689,},
            {527.3008, 210.7656, 494.2224, 403.8657, 255.6551, 104.6478, 1049.266, 1301.511, 198.9058, 1125.041, 768.1641, 2125.512, 651.1179, 1015.165, 728.3743, 918.5615, 328.7515, 273.4106, 1814.83, 0, 552.4229, 2253.211, 2128.828, 875.2542, 194.5945,},
            {483.4673, 736.3755, 1043.484, 255.8823, 307.3289, 491.1125, 537.6206, 780.9512, 450.2585, 677.0608, 229.4867, 1582.369, 254.9977, 1065.599, 450.3982, 601.9917, 880.5469, 818.1228, 1264.193, 552.4229, 0, 1735.937, 1712.136, 871.6396, 706.5024,},
            {2140.978, 2456.263, 2703.402, 1853.617, 2036.128, 2164.855, 1493.843, 955.802, 2086.845, 1649.619, 1506.451, 361.5388, 1808.52, 2591.447, 1589.835, 1916.578, 2574.082, 2526.562, 661.6543, 2253.211, 1735.937, 0, 694.9363, 2404.839, 2430.269,},
            {2184.402, 2339.509, 2503.828, 1733.132, 1967.256, 2027.319, 1686.675, 1024.566, 1936.304, 1891.166, 1503.794, 986.8149, 1872.696, 2725.79, 1401.321, 2090.089, 2415.489, 2388.689, 1129.327, 2128.828, 1712.136, 694.9363, 0, 2528.479, 2321.873,},
            {408.1648, 844.1663, 1188.549, 1005.761, 775.239, 933.196, 912.2104, 1519.174, 992.3379, 795.2136, 1038.624, 2157.517, 660.5173, 197.8015, 1311.21, 496.4224, 1008.2, 926.6267, 1800.098, 875.2542, 871.6396, 2404.839, 2528.479, 0, 813.5513,},
            {540.7388, 36.4947, 405.7886, 592.0278, 399.2253, 298.8486, 1161.676, 1475.479, 392.9045, 1205.747, 931.7148, 2288.748, 751.4614, 923.2229, 922.3145, 963.0435, 215.561, 132.7684, 1968.689, 194.5945, 706.5024, 2430.269, 2321.873, 813.5513, 0,},
    };
}