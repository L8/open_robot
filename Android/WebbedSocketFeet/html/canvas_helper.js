

function drawWADS(pressedCode, canvas) {
   
    var context = canvas.getContext('2d');
      width = canvas.getWidth();
      height = canvas.getHeight();
      
      context.fillStyle   = '#00f';
      context.strokeStyle = '#f00';
      context.lineWidth   = 4;
      
      leftCode = 97;
      upCode = 119;
      rightCode = 100;
      downCode = 115;
      
      normalFillStyle = '#999';
      normalStrokeStyle = '#AAA';
      
      pressedFillStyle = '#00f';
      pressedStrokeStyle = '#f00';
      
      padding = 5;
      shortTriangleDimension = width / 6;
      longTriangleDimension = width / 3;
      
    // draw left arrow
    context.beginPath();
    
    context.fillStyle = pressedCode == leftCode ? pressedFillStyle : normalFillStyle;
    context.strokeStyle = pressedCode === leftCode ? pressedStrokeStyle : normalStrokeStyle;
    
    leftVertX = padding;
    leftVertY = height / 2;
    context.moveTo(leftVertX, leftVertY);
    
    topVertX = leftVertX + shortTriangleDimension;
    topVertY = leftVertY - longTriangleDimension / 2;
    context.lineTo(topVertX, topVertY);
    
    bottomVertX = topVertX;
    bottomVertY = leftVertY + longTriangleDimension / 2;
    context.lineTo(bottomVertX, bottomVertY);
    context.closePath();
    
    context.fill();
    context.stroke();

    
    // draw top arrow
     context.beginPath();
     
     context.fillStyle = pressedCode == upCode ? pressedFillStyle : normalFillStyle;
     context.strokeStyle = pressedCode === upCode ? pressedStrokeStyle : normalStrokeStyle;

      topVertX = width / 2
      topVertY = padding;
      context.moveTo(topVertX, topVertY);
        
      leftVertX = topVertX - longTriangleDimension / 2;
      leftVertY = topVertY + shortTriangleDimension;
      context.lineTo(leftVertX, leftVertY);

       rightVertX = leftVertX + longTriangleDimension;
       rightVertY = leftVertY;
       context.lineTo(rightVertX, rightVertY);

       context.closePath();

       context.fill();
       context.stroke();
       
       // draw right arrow
        context.beginPath();
        
        context.fillStyle = pressedCode == rightCode ? pressedFillStyle : normalFillStyle;
        context.strokeStyle = pressedCode === rightCode ? pressedStrokeStyle : normalStrokeStyle;
        
          rightVertX = width - padding;
           rightVertY = height / 2;
           context.moveTo(rightVertX, rightVertY);

         topVertX = rightVertX - shortTriangleDimension;
         topVertY = rightVertY - longTriangleDimension / 2;
         context.lineTo(topVertX, topVertY);

          bottomVertX = topVertX
          bottomVertY = topVertY + longTriangleDimension
          context.lineTo(bottomVertX, bottomVertY);
        
          context.closePath();

          context.fill();
          context.stroke();
       

          // draw bottom arrow
           context.beginPath();

           context.fillStyle = pressedCode == downCode ? pressedFillStyle : normalFillStyle;
           context.strokeStyle = pressedCode === downCode ? pressedStrokeStyle : normalStrokeStyle;
           
            bottomVertX = width / 2
            bottomVertY = height - padding;
            context.moveTo(bottomVertX, bottomVertY);

            leftVertX = bottomVertX - longTriangleDimension / 2;
            leftVertY = bottomVertY - shortTriangleDimension;
            context.lineTo(leftVertX, leftVertY);

             rightVertX = leftVertX + longTriangleDimension;
             rightVertY = leftVertY;
             context.lineTo(rightVertX, rightVertY);

             context.closePath();

             context.fill();
             context.stroke();
}