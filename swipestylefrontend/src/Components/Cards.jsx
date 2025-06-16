/* eslint-disable no-unused-vars */
import { useCallback,useMemo } from "react";
import {motion, useMotionValue, useMotionValueEvent, useTransform} from "framer-motion";
const Cards = ({clothing,clothingData,setClothingData,index}) => {
    const {
        altText,
        gender,
        imageUrl,
        name,
        price,
        productId,
        productUrl,
    } = clothing;
    const x = useMotionValue(0);
    const opacity = useTransform(x,[-150,0,150],[0.3,1,0.3]);
    const rotateRaw = useTransform(x,[-150,150],[-18,18]);
    const isFrontCard = useMemo(() => {
        return productId === clothingData[clothingData.length - 1]?.productId;
    }, [productId, clothingData]);
    const staticOffset = useMemo(() => {
        return isFrontCard ? 0 : index % 2 === 0 ? -6 : 6;
    }, [isFrontCard, index]);
    const rotate = useTransform(rotateRaw, (latest) => `${latest + staticOffset}deg`);
    
    const handleDragEnd = useCallback(() => {
        const currentX = x.get();
        if (Math.abs(currentX) > 50) {
            setClothingData((prev) => prev.filter((item) => item.productId !== productId));
        }
    }, [x, setClothingData, productId]);
    return (
            <motion.img src={imageUrl} alt={altText} className="rounded-lg h-96 w-72 hover:cursor-grab active:cursor-grabbing origin-bottom" 
            style={{
                gridRow:1,
                gridColumn:1,
                x,
                opacity,
                rotate,
                zIndex: isFrontCard ? 10 : index,
                transition:"0.125s transform"
            }}
            animate={{
                scale: isFrontCard?1:0.98, 
                y: isFrontCard ? 0 : index * 4,
            }}
            whileHover={{ 
                scale: isFrontCard ? 1.02 : 0.99,
                boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.25)"
            }}
            whileDrag={{ 
                scale: 1.05,
                boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.4)"
            }}
            transition={{ 
                type: "spring", 
                stiffness: 300, 
                damping: 30 
            }}
            drag="x"
            dragConstraints={{
                left:0,
                right:0
            }}
            onDragEnd={handleDragEnd}

            />
    )
}
export default Cards;