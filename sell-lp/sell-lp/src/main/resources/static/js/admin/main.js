document.addEventListener("DOMContentLoaded", function () {
    // 1. Đọc dữ liệu từ biến toàn cục do file HTML truyền sang
    const revenueData = window.dashboardRevenueData || [];
    const dayLabels = window.dashboardDayLabels || [];
    const statusData = window.dashboardStatusData || [0, 0, 0];

    // =========================================================================
    // BIỂU ĐỒ XU HƯỚNG DOANH THU (LINE CHART)
    // =========================================================================
    const canvasRevenue = document.getElementById('revenueChart');
    if (canvasRevenue) {
        const ctxRevenue = canvasRevenue.getContext('2d');
        new Chart(ctxRevenue, {
            type: 'line',
            data: {
                labels: dayLabels,
                datasets: [{
                    label: 'Doanh thu',
                    data: revenueData,
                    borderColor: '#4361ee',
                    backgroundColor: 'rgba(67, 97, 238, 0.1)',
                    fill: true,
                    tension: 0.4,
                    pointRadius: 6,
                    pointBackgroundColor: '#4361ee'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                layout: {
                    padding: { top: 20 }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 180000000,
                        ticks: {
                            stepSize: 2000000,
                            callback: function (value) {
                                const milestones = [10000000, 50000000, 100000000];
                                if (milestones.includes(value)) return (value / 1000000) + ' Tr';
                                if (value === 0) return '0';
                                return null;
                            }
                        },
                        grid: {
                            drawBorder: false,
                            color: function (context) {
                                const val = context.tick.value;
                                if ([10000000, 50000000, 100000000].includes(val)) return 'rgba(0, 0, 0, 0.1)';
                                return 'transparent';
                            }
                        }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: '#94a3b8' }
                    }
                },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        enabled: true,
                        callbacks: {
                            label: function (context) {
                                return ' ' + context.parsed.y.toLocaleString('vi-VN') + ' VND';
                            }
                        }
                    }
                }
            }
        });
    }

    // =========================================================================
    // BIỂU ĐỒ TRẠNG THÁI ĐƠN HÀNG (DOUGHNUT CHART)
    // =========================================================================
    const canvasStatus = document.getElementById('statusChart');
    if (canvasStatus) {
        const ctxStatus = canvasStatus.getContext('2d');
        new Chart(ctxStatus, {
            type: 'doughnut',
            data: {
                labels: ['Thành công', 'Chờ xử lý', 'Đã hủy'],
                datasets: [{
                    data: statusData,
                    backgroundColor: ['#2ecc71', '#ff9f1c', '#e71d36']
                }]
            },
            options: {
                cutout: '70%',
                plugins: {
                    legend: { position: 'bottom' }
                }
            }
        });
    }
});