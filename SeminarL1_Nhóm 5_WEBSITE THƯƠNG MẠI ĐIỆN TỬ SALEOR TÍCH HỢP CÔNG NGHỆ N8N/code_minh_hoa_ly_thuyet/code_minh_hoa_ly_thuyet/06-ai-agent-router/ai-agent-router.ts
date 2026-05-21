// Minh họa AI Agent trung tâm điều phối yêu cầu đến các agent chuyên biệt
// Dùng trong phần lý thuyết: AI Agent cá thể hóa + n8n workflow

type AgentName =
  | 'ProductConsultingAgent'
  | 'FashionRecommendationAgent'
  | 'CartReminderAgent'
  | 'OrderSupportAgent'
  | 'UserBehaviorAnalysisAgent'
  | 'ContentGenerationAgent';

type RoutingResult = {
  agent: AgentName;
  reason: string;
};

const routingRules: Array<{ keywords: string[]; agent: AgentName; reason: string }> = [
  {
    keywords: ['size', 'màu', 'chất liệu', 'còn hàng', 'giá', 'sản phẩm'],
    agent: 'ProductConsultingAgent',
    reason: 'Người dùng đang hỏi thông tin sản phẩm.',
  },
  {
    keywords: ['phối đồ', 'mặc với', 'gợi ý outfit', 'hợp với'],
    agent: 'FashionRecommendationAgent',
    reason: 'Người dùng cần gợi ý phối đồ hoặc đề xuất sản phẩm phù hợp.',
  },
  {
    keywords: ['giỏ hàng', 'chưa thanh toán', 'bỏ quên'],
    agent: 'CartReminderAgent',
    reason: 'Yêu cầu liên quan đến giỏ hàng chưa hoàn tất.',
  },
  {
    keywords: ['đơn hàng', 'vận chuyển', 'thanh toán', 'trạng thái'],
    agent: 'OrderSupportAgent',
    reason: 'Yêu cầu liên quan đến đơn hàng hoặc thanh toán.',
  },
  {
    keywords: ['hành vi', 'xem nhiều', 'mua nhiều', 'thống kê'],
    agent: 'UserBehaviorAnalysisAgent',
    reason: 'Yêu cầu phân tích hành vi người dùng.',
  },
  {
    keywords: ['viết mô tả', 'tạo nội dung', 'email', 'thông báo'],
    agent: 'ContentGenerationAgent',
    reason: 'Yêu cầu tạo nội dung tự động.',
  },
];

export function routeToAgent(userMessage: string): RoutingResult {
  const message = userMessage.toLowerCase();

  for (const rule of routingRules) {
    const matched = rule.keywords.some((keyword) => message.includes(keyword));
    if (matched) {
      return { agent: rule.agent, reason: rule.reason };
    }
  }

  return {
    agent: 'ProductConsultingAgent',
    reason: 'Không xác định rõ ý định, chuyển về agent tư vấn sản phẩm mặc định.',
  };
}

// Ví dụ chạy thử
const samples = [
  'Áo này còn size M không?',
  'Áo sơ mi này phối với quần gì?',
  'Đơn hàng của tôi đang ở đâu?',
  'Viết mô tả ngắn cho sản phẩm áo khoác denim',
];

for (const sample of samples) {
  console.log(sample, '=>', routeToAgent(sample));
}
