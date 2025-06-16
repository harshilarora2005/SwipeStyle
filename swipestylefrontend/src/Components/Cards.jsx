/* eslint-disable no-unused-vars */
import {motion, useMotionValue, useMotionValueEvent, useTransform} from "framer-motion";
const Cards = ({clothing,clothingData,setClothingData}) => {
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
    const rotate = useTransform(x,[-150,150],[-18,18]);
    const handleDragEnd = () => {
        if(Math.abs(x.get())>50){
            setClothingData((pv)=>pv.filter((v)=> v.productId!=productId))
        }
    }
    return (
            <motion.img src={imageUrl} alt={altText} className="rounded-lg h-96 w-72 hover:cursor-grab active:cursor-grabbing" 
            style={{
                gridRow:1,
                gridColumn:1,
                x,
                opacity,
                rotate
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