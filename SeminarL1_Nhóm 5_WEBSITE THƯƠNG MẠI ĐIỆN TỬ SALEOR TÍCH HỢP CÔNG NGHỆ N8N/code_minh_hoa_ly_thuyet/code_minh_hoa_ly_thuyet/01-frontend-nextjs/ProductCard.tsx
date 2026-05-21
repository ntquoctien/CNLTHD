// Minh họa Frontend Next.js/React: component hiển thị sản phẩm
// Có thể đặt trong my-app/components/ProductCard.tsx

type ProductCardProps = {
  id: string;
  name: string;
  price: number;
  imageUrl: string;
  stock: number;
  onAddToCart?: (productId: string) => void;
};

export default function ProductCard({
  id,
  name,
  price,
  imageUrl,
  stock,
  onAddToCart,
}: ProductCardProps) {
  const formatPrice = (value: number) =>
    new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(value);

  return (
    <div className="rounded-xl border p-4 shadow-sm bg-white">
      <img
        src={imageUrl}
        alt={name}
        className="h-48 w-full rounded-lg object-cover"
      />

      <div className="mt-3">
        <h3 className="text-lg font-semibold text-gray-900">{name}</h3>
        <p className="mt-1 text-blue-600 font-bold">{formatPrice(price)}</p>
        <p className="text-sm text-gray-500">Tồn kho: {stock}</p>
      </div>

      <button
        disabled={stock <= 0}
        onClick={() => onAddToCart?.(id)}
        className="mt-4 w-full rounded-lg bg-blue-600 py-2 text-white disabled:bg-gray-400"
      >
        {stock > 0 ? 'Thêm vào giỏ hàng' : 'Hết hàng'}
      </button>
    </div>
  );
}
