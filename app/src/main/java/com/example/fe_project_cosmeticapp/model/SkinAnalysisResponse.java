package com.example.fe_project_cosmeticapp.model;

import com.google.gson.annotations.SerializedName;

public class SkinAnalysisResponse {
    @SerializedName("request_id")
    private String requestId;

    @SerializedName("time_used")
    private int timeUsed;

    @SerializedName("error_message")
    private String errorMessage;

    @SerializedName("result")
    private ResultInfo result;

    public String getRequestId() {
        return requestId;
    }

    public int getTimeUsed() {
        return timeUsed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ResultInfo getResult() {
        return result;
    }

    public SkinTypeInfo getSkinType() {
        return result != null ? result.getSkinType() : null;
    }

    // Class con để chứa thông tin kết quả phân tích
    public static class ResultInfo {
        @SerializedName("skin_type")
        private SkinTypeInfo skinType;

        @SerializedName("acne")
        private AttributeInfo acne;

        @SerializedName("dark_circle")
        private AttributeInfo darkCircle;

        @SerializedName("skin_spot")
        private AttributeInfo skinSpot;

        @SerializedName("eye_pouch")
        private AttributeInfo eyePouch;

        // Thêm các thuộc tính khác nếu cần

        public SkinTypeInfo getSkinType() {
            return skinType;
        }

        public AttributeInfo getAcne() {
            return acne;
        }

        public AttributeInfo getDarkCircle() {
            return darkCircle;
        }

        public AttributeInfo getSkinSpot() {
            return skinSpot;
        }

        public AttributeInfo getEyePouch() {
            return eyePouch;
        }
    }

    // Class con để chứa thông tin loại da
    public static class SkinTypeInfo {
        @SerializedName("skin_type")
        private int skinTypeValue;

        @SerializedName("details")
        private SkinTypeDetails details;

        // Getters và setters
        public int getSkinTypeValue() {
            return skinTypeValue;
        }

        public SkinTypeDetails getDetails() {
            return details;
        }

        // Chuyển đổi từ giá trị số sang mô tả loại da
        public String getSkinTypeDescription() {
            switch (skinTypeValue) {
                case 0:
                    return "Da thường";
                case 1:
                    return "Da khô";
                case 2:
                    return "Da dầu";
                case 3:
                    return "Da hỗn hợp";
                default:
                    return "Không xác định";
            }
        }

        // Lấy độ tin cậy từ details dựa trên loại da hiện tại
        public double getConfidence() {
            if (details != null) {
                String skinTypeStr = String.valueOf(skinTypeValue);
                if (details.getConfidenceMap().containsKey(skinTypeStr)) {
                    return details.getConfidenceMap().get(skinTypeStr).confidence;
                }
            }
            return 0.0;
        }
    }

    // Class con chứa chi tiết về các loại da và độ tin cậy
    public static class SkinTypeDetails {
        @SerializedName("0")
        private TypeConfidence normal;

        @SerializedName("1")
        private TypeConfidence dry;

        @SerializedName("2")
        private TypeConfidence oily;

        @SerializedName("3")
        private TypeConfidence combination;

        // Phương thức giúp lấy map để dễ dàng truy cập theo key
        public java.util.Map<String, TypeConfidence> getConfidenceMap() {
            java.util.Map<String, TypeConfidence> map = new java.util.HashMap<>();
            map.put("0", normal);
            map.put("1", dry);
            map.put("2", oily);
            map.put("3", combination);
            return map;
        }
    }

    // Class con chứa thông tin về độ tin cậy của mỗi loại da
    public static class TypeConfidence {
        @SerializedName("confidence")
        public double confidence;

        @SerializedName("value")
        public int value;
    }

    // Class con chứa thông tin về các thuộc tính của da như mụn, nốt đồi mồi,...
    public static class AttributeInfo {
        @SerializedName("confidence")
        private double confidence;

        @SerializedName("value")
        private int value;

        public double getConfidence() {
            return confidence;
        }

        public int getValue() {
            return value;
        }
    }
}
