function StatCard({ title, value, onClick }) {
  return (
    <button className="stat-card clickable-card" onClick={onClick}>
      <p>{title}</p>
      <h2>{value}</h2>
    </button>
  );
}

export default StatCard;