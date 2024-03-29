function plotIt(datasets) {
    var ctx = document.getElementById('myChart').getContext('2d');
    var chart = new Chart(ctx, {
        type: 'scatter',
        data: datasets,

        options: {
            legend: {
                  display: true,
                  position: 'bottom',
                  labels: {
                    fontColor: "#000080",
                  }
            },
            tooltips: {
                callbacks: {
                    label: function(tooltipItem, data) {
                        return data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index].tooltip;
                    }
                }
            }
        }
    });
}

var stack = [];
stack.push({'r': 255, 'g': 99, 'b': 132});
stack.push({'r': 209, 'g': 100, 'b': 255});
stack.push({'r': 100, 'g': 214, 'b': 255});
stack.push({'r': 255, 'g': 100, 'b': 225});
stack.push({'r': 246, 'g': 255, 'b': 100});
stack.push({'r': 255, 'g': 183, 'b': 100});
stack.push({'r': 100, 'g': 162, 'b': 255});
stack.push({'r': 100, 'g': 255, 'b': 200});

brandAndFilters = window.location.pathname.substring(1);
const url = window.location.origin + '/json-data/' + brandAndFilters;
fetch(url)
    .then((resp) => resp.json())
    .then(function(data) {
        let carsByMonth = data.dataByMonth;

        var listOfDatasets = []

        for (const [month, cars] of Object.entries(carsByMonth)) {
            xyPoints = cars.map(function(each) {
                return {'x': each.milage, 'y': each.price, 'tooltip': each.tooltip};
            });

            carsByMonth[month] = xyPoints;

            let colorCode = stack.pop();
            let color = 'rgb(' + colorCode['r'] + ',' + colorCode['g'] + ',' + colorCode['b'] + ')';
            let dataset = {"label": month, "data": xyPoints, "backgroundColor": color};
            listOfDatasets.push(dataset);
        }

        var datasets = {"datasets": listOfDatasets};
        plotIt(datasets);
    })
    .catch(function(error) {
    console.log(JSON.stringify(error));
});