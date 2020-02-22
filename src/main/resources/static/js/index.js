
var defaultSpeed = 17;
var defaultMoveDistance = 750;
var leftBoundary = 0;
var rightBoundary = -2250;
var provinceDetailObj = {
    '湖北': "hb", 
    '广东': "gd", 
    '河南': "henan", 
    '浙江': "zj", 
    '湖南': "hn", 
    '安徽': "ah", 
    '江西': "jiangxi", 
    '江苏': "jiangsu", 
    '重庆': "cq",
    '山东': "sd", 
    '四川': "cd", 
    '黑龙江': "heilongjiang", 
    '北京': "bj", 
    '上海': "sh", 
    '河北': "hebei", 
    '福建': "fj", 
    '广西': "guangxi",
    '陕西': "xian", 
    '云南': "yn", 
    '海南': 'hainan',
    '贵州': "guizhou", 
    '山西': "shanxi", 
    '天津': "tj", 
    '辽宁': "ln", 
    '甘肃': "gansu", 
    '吉林': "jilin",
    '新疆': "xinjiang", 
    '内蒙古': "neimenggu", 
    '宁夏': "ningxia", 
    '香港': "hk", 
    '台湾': "taiwan", 
    '青海': "qinghai", 
    '澳门': "macau", 
    '西藏': "xizang"
};
init();
function init() {
    $('.placeItemWrap').eq(0).addClass('current');
    addChartMoveEvent();
    addIndexButtonEvent();
    showCityData();
    detailLinked();
}
function addChartMoveEvent() {
    var countryChartElement = $('.chart .country').eq(0)[0];
    var contrastChartElement = $('.chart .contrast').eq(0)[0];
    $('.country-left').eq(0).on('click', function(){
        $('.country-right').eq(0)[0].style.display = 'block';
        var oldActive = $('.country-chart-index .active').eq(0);
        newIndexActive(oldActive, $(oldActive).prev());
        moveTimeInterval(countryChartElement, this, defaultSpeed, defaultMoveDistance, leftBoundary);
    });
    $('.country-right').eq(0).on('click', function(){
        $('.country-left').eq(0)[0].style.display = 'block';
        var oldActive = $('.country-chart-index .active').eq(0);
        newIndexActive(oldActive, $(oldActive).next());
        moveTimeInterval(countryChartElement, this, -defaultSpeed, -defaultMoveDistance, rightBoundary);
    });
    $('.contrast-left').eq(0).on('click', function(){
        $('.contrast-right').eq(0)[0].style.display = 'block';
        var oldActive = $('.contrast-chart-index .active').eq(0);
        newIndexActive(oldActive, $(oldActive).prev());
        moveTimeInterval(contrastChartElement, this, defaultSpeed, defaultMoveDistance, leftBoundary);
    });
    $('.contrast-right').eq(0).on('click', function(){
        $('.contrast-left').eq(0)[0].style.display = 'block';
        var oldActive = $('.contrast-chart-index .active').eq(0);
        newIndexActive(oldActive, $(oldActive).next());
        moveTimeInterval(contrastChartElement, this, -defaultSpeed, -defaultMoveDistance, rightBoundary);
    });
}

function newIndexActive(oldActive, newActive) {
    oldActive.removeClass('active');
    newActive.addClass('active')
}

function moveTimeInterval(targetElement, button, speed, moveDistance, boundary) {
    button.style.display = 'none';
    var leftDis = targetElement.offsetLeft;
    var timer = setInterval(function(){
        targetElement.style.left = targetElement.offsetLeft + 'px';
        targetElement.style.left = parseInt(targetElement.style.left) + speed + 'px';
        if(Math.abs(leftDis - parseInt(targetElement.style.left)) >= (moveDistance > 0 ? moveDistance : -moveDistance)) {
            targetElement.style.left = leftDis + moveDistance + 'px';
            if(parseInt(targetElement.style.left) != boundary) button.style.display = 'block';
            clearInterval(timer);
        }
    }, 10);
} 

function recoverIndexButton(buttons) {
    for(var i = 0; i < buttons.length; i ++) {
        $(buttons[i]).removeClass('active');
    }
}

function handleIndexButton(oldActive, newActive, targetElement) {
    newIndexActive(oldActive, newActive);
    var chartElement = $(targetElement).parent();
    var oldActiveNum = $(oldActive).attr('num');
    var newActiveNum = $(newActive).attr('num');
    var indexDis = oldActiveNum - newActiveNum;
    var moveDistance = indexDis * defaultMoveDistance;
    var speed = indexDis * defaultSpeed;
    if(newActiveNum > oldActiveNum) {
        var button = $(chartElement).next()[0];
        $(chartElement).prev()[0].style.display = 'block';
        moveTimeInterval(targetElement, button, speed, moveDistance, rightBoundary);
    }else if (newActiveNum < oldActiveNum){
        var button = $(chartElement).prev()[0];
        $(chartElement).next()[0].style.display = 'block';
        moveTimeInterval(targetElement, button, speed, moveDistance, leftBoundary);
    }else {
        return;
    }
}
function indexButtonEvent(buttonsArray, activeClassName, targetElement) {
    for(var i = 0; i < buttonsArray.length; i ++) {
        $(buttonsArray[i]).attr('num', i + 1);
        $(buttonsArray[i]).on('click', function() {
            var oldActive = $(activeClassName).eq(0);
            var newActive = $(this);
            handleIndexButton(oldActive, newActive, targetElement);
        });
    }
}
function addIndexButtonEvent() {
    var countryButtons = $('.country-btn');
    indexButtonEvent(countryButtons, '.country-chart-index .active', $('.chart .country').eq(0)[0]);

    var contrastButtons = $('.contrast-btn');
    indexButtonEvent(contrastButtons, '.contrast-chart-index .active', $('.chart .contrast').eq(0)[0]);
}
function showCityData() {
    var showButton = $('.placeArea h2');
    for(var i = 0; i < showButton.length; i ++) {
        $(showButton[i]).on('click', function(){
            var province = $(this).parent().parent();
            if(province.hasClass('current')) province.removeClass('current');
            else province.addClass('current');
        });
    }
}
function detailLinked() {
    var detailDiv = $('.placeArea .detail');
    for(var i = 0; i < detailDiv.length; i ++) {
        var detail = $(detailDiv[i]);
        var aLink = detail.children()[0];
        var provinceName = detail.prevAll()[4].text;
        aLink.href = 'https://news.qq.com/hdh5/feiyanarea.htm#/area/?pool=' + provinceDetailObj[provinceName];
    }
}