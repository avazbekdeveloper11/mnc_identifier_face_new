import 'dart:convert';

LivenessDetectionResult livenessDetectionResultFromJson(String str) =>
    LivenessDetectionResult.fromJson(json.decode(str));

String livenessDetectionResultToJson(LivenessDetectionResult data) => json.encode(data.toJson());

class LivenessDetectionResult {
  LivenessDetectionResult({
    this.attempt,
    required this.detectionResult,
    this.errorMessage,
    this.isSuccess,
    this.totalTimeMilis,
  });

  final int? attempt;
  final List<DetectionResult> detectionResult;
  final String? errorMessage;
  final bool? isSuccess;
  final int? totalTimeMilis;

  factory LivenessDetectionResult.fromJson(Map<String, dynamic> json) {
    return LivenessDetectionResult(
      attempt: json["attempt"],
      detectionResult: json["detectionResult"] != null && json["detectionResult"] is List
          ? (json["detectionResult"] as List<dynamic>).map((x) => DetectionResult.fromJson(x)).toList()
          : [],
      errorMessage: json["errorMessage"] ?? "",
      isSuccess: json["isSuccess"],
      totalTimeMilis: json["totalTimeMilis"] ?? json["totalTimeMillis"],
    );
  }

  Map<String, dynamic> toJson() => {
        "attempt": attempt,
        "detectionResult": detectionResult.map((x) => x.toJson()).toList(),
        "errorMessage": errorMessage,
        "isSuccess": isSuccess,
        "totalTimeMilis": totalTimeMilis,
      };
}

class DetectionResult {
  DetectionResult({
    this.detectionMode,
    this.imagePath,
    this.timeMilis,
  });

  final String? detectionMode;
  final String? imagePath;
  final int? timeMilis;

  factory DetectionResult.fromJson(Map<String, dynamic> json) => DetectionResult(
        detectionMode: json["detectionMode"],
        imagePath: json["imagePath"]?.toString(),
        timeMilis: json["timeMilis"] ?? 1000,
      );

  Map<String, dynamic> toJson() => {
        "detectionMode": detectionMode,
        "imagePath": imagePath,
        "timeMilis": timeMilis,
      };
}
