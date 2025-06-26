import { useRef } from 'react';
import { useScroll, useTransform } from 'framer-motion';

const useCardAnimations = () => {
    const scrollRef = useRef(null);
    const cardRefs = useRef({});
    const { scrollYProgress } = useScroll({ target: scrollRef });

    const detailsY = useTransform(scrollYProgress, [0, 1], [50, 0]);
    const detailsOpacity = useTransform(scrollYProgress, [0, 0.5, 1], [0, 1, 1]);
    const detailsScale = useTransform(scrollYProgress, [0, 0.5, 1], [0.8, 0.9, 1]);

    const animateCardSwipe = (productId, direction) => {
        if (cardRefs.current[productId]) {
        cardRefs.current[productId].animateSwipe(direction);
        }
    };

    return {
        scrollRef,
        cardRefs,
        detailsY,
        detailsOpacity,
        detailsScale,
        animateCardSwipe
    };
};

export default useCardAnimations ;